package com.collegeclub.service.impl;

import com.collegeclub.dao.ClubDao;
import com.collegeclub.dao.MembershipDao;
import com.collegeclub.dao.impl.ClubDaoImpl;
import com.collegeclub.dao.impl.MembershipDaoImpl;
import com.collegeclub.exception.ClubNotFoundException;
import com.collegeclub.exception.ClubSystemException;
import com.collegeclub.exception.DuplicateMembershipException;
import com.collegeclub.exception.InvalidInputException;
import com.collegeclub.exception.MembershipLimitReachedException;
import com.collegeclub.model.Club;
import com.collegeclub.model.User;
import com.collegeclub.service.ClubOperations;

import java.sql.SQLException;
import java.util.List;

public class ClubService implements ClubOperations {

    private final ClubDao clubDao;
    private final MembershipDao membershipDao;

    public ClubService() {
        this(new ClubDaoImpl(), new MembershipDaoImpl());
    }

    public ClubService(ClubDao clubDao, MembershipDao membershipDao) {
        this.clubDao = clubDao;
        this.membershipDao = membershipDao;
        seedAll30Clubs();
    }

    @Override
    public Club createClub(Club club) throws InvalidInputException, ClubSystemException {
        if (club == null) {
            throw new InvalidInputException("Club data cannot be null.");
        }
        if (club.getName() == null || club.getName().trim().length() < 3) {
            throw new InvalidInputException("Club name must contain at least 3 characters.");
        }
        if (club.getCoordinator() == null || club.getCoordinator().trim().isEmpty()) {
            throw new InvalidInputException("Coordinator name is required.");
        }

        try {
            if (clubDao.findByName(club.getName()).isPresent()) {
                throw new DuplicateMembershipException("A club with name '" + club.getName() + "' already exists.");
            }
            return clubDao.save(club);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error saving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public Club getClubById(int id) throws ClubNotFoundException, ClubSystemException {
        try {
            return clubDao.findById(id)
                    .orElseThrow(() -> new ClubNotFoundException("Club with ID " + id + " not found."));
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> getAllClubs() throws ClubSystemException {
        try {
            return clubDao.findAll();
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving clubs: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> searchClubs(String query, String category) throws ClubSystemException {
        try {
            return clubDao.search(query, category);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error searching clubs: " + e.getMessage(), 500, e);
        }
    }

    public List<Club> searchClubs(String query) throws ClubSystemException {
        return searchClubs(query, null);
    }

    public List<Club> getClubsByCategory(String category) throws ClubSystemException {
        return searchClubs(null, category);
    }

    public List<Club> getStudentClubs(int userId) throws ClubSystemException {
        return getUserClubs(userId);
    }

    public Club createClub(String name, String category, String description, int coordinatorId, int capacity) throws ClubSystemException, InvalidInputException {
        Club c = new Club(name, description, category, "Faculty Coordinator", capacity);
        return createClub(c);
    }

    @Override
    public boolean joinClub(int clubId, int userId) throws ClubNotFoundException, DuplicateMembershipException, MembershipLimitReachedException, ClubSystemException {
        Club club = getClubById(clubId);

        try {
            if (membershipDao.isMember(clubId, userId)) {
                throw new DuplicateMembershipException("You are already a member of this club.");
            }

            int currentCount = membershipDao.countMembers(clubId);
            if (club.getCapacity() > 0 && currentCount >= club.getCapacity()) {
                throw new MembershipLimitReachedException("Club has reached its maximum capacity of " + club.getCapacity() + " members.");
            }

            return membershipDao.addMembership(clubId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error joining club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean leaveClub(int clubId, int userId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId); // verifies existence

        try {
            if (!membershipDao.isMember(clubId, userId)) {
                throw new ClubSystemException("NOT_A_MEMBER", "User is not currently a member of this club.", 400);
            }
            return membershipDao.removeMembership(clubId, userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error leaving club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean updateClub(Club club) throws ClubNotFoundException, InvalidInputException, ClubSystemException {
        if (club == null || club.getId() <= 0) {
            throw new InvalidInputException("Valid Club ID is required for update.");
        }
        getClubById(club.getId()); // ensure exists

        try {
            return clubDao.update(club);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error updating club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public boolean deleteClub(int clubId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId); // ensure exists
        try {
            return clubDao.delete(clubId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error deleting club: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<Club> getUserClubs(int userId) throws ClubSystemException {
        try {
            return membershipDao.findClubsByUserId(userId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving user clubs: " + e.getMessage(), 500, e);
        }
    }

    @Override
    public List<User> getClubMembers(int clubId) throws ClubNotFoundException, ClubSystemException {
        getClubById(clubId);
        try {
            return membershipDao.findMembersByClubId(clubId);
        } catch (SQLException e) {
            throw new ClubSystemException("DATABASE_ERROR", "Error retrieving club members: " + e.getMessage(), 500, e);
        }
    }

    private void seedAll30Clubs() {
        try {
            Club[] defaultClubs = new Club[] {
                // 1. Coding & Open Source Club
                new Club(0, "Coding & Open Source Club",
                        "Competitive programming, data structures, algorithms, open source contributions, and hackathon training.",
                        "Technical", "Dr. S. Arul", 120,
                        "Fostering coding proficiency, competitive programming skills, and open-source software collaboration.",
                        "Weekly DSA contests, Git workshops, Hackathons, LeetCode sprints", "💻"),

                // 2. Artificial Intelligence & Robotics Club
                new Club(0, "Artificial Intelligence & Robotics Club",
                        "Machine learning, neural networks, computer vision, autonomous bots, and embedded robotics systems.",
                        "Technical", "Prof. K. Ramesh", 100,
                        "Empowering students to develop intelligent agents, deep learning models, and autonomous robotic prototypes.",
                        "Robotics bootcamps, Kaggle competitions, ROS hands-on labs, Bot-wars", "🤖"),

                // 3. Cyber Security & Ethical Hacking Club
                new Club(0, "Cyber Security & Ethical Hacking Club",
                        "Penetration testing, network defense, digital forensics, CTF challenges, and secure coding practices.",
                        "Technical", "Dr. V. Priya", 90,
                        "Building cyber defense capabilities and ethical hacking awareness across modern software architectures.",
                        "Capture The Flag (CTF) meets, Kali Linux workshops, Web Security drills", "🛡️"),

                // 4. Web & Mobile App Development Club
                new Club(0, "Web & Mobile App Development Club",
                        "Full-stack development, modern web architectures, React, Flutter, REST APIs, and cloud deployments.",
                        "Technical", "Prof. A. Kumar", 100,
                        "Training students in industry-standard responsive frontend and robust backend system design.",
                        "Full-stack project sprints, App design showcases, UI/UX bootcamps", "📱"),

                // 5. Cloud Computing & DevOps Society
                new Club(0, "Cloud Computing & DevOps Society",
                        "Containerization, CI/CD pipelines, Kubernetes, AWS, Google Cloud, and site reliability engineering.",
                        "Technical", "Dr. N. Sharma", 80,
                        "Mastering cloud infrastructure provisioning, Docker containers, and automated deployment architectures.",
                        "Docker crash courses, Kubernetes clusters setup, Cloud certification study groups", "☁️"),

                // 6. Data Science & Big Data Analytics Club
                new Club(0, "Data Science & Big Data Analytics Club",
                        "Exploratory data analysis, statistical modeling, Hadoop, Spark, BigQuery, and visual dashboards.",
                        "Technical", "Dr. M. Jayakumar", 85,
                        "Transforming raw datasets into actionable intelligence through modern analytical pipelines.",
                        "Data visualization hackathons, Python pandas masterclasses, Big Data workshops", "📊"),

                // 7. Internet of Things (IoT) & Embedded Systems Club
                new Club(0, "Internet of Things (IoT) & Embedded Systems Club",
                        "Arduino, Raspberry Pi, sensor networks, smart automation, and edge hardware interfacing.",
                        "Technical", "Prof. R. Balaji", 75,
                        "Interfacing physical environments with smart microcontroller nodes and cloud telemetry.",
                        "Smart campus IoT buildathons, Embedded C firmware labs, Sensor interfacing clinics", "🔌"),

                // 8. Literary & Debating Society
                new Club(0, "Literary & Debating Society",
                        "Parliamentary debates, elocution, creative writing, poetry recitals, and critical rhetoric.",
                        "Literary", "Dr. E. Elizabeth", 100,
                        "Developing articulation, critical thinking, public speaking prowess, and expressive writing.",
                        "Mock Parliamentary Debates, Creative writing symposiums, Oratory contests", "📚"),

                // 9. Dramatics & Theatre Club
                new Club(0, "Dramatics & Theatre Club",
                        "Stage plays, street theatre (nukkad natak), scripting, voice modulation, and stagecraft.",
                        "Cultural", "Prof. S. Manoharan", 70,
                        "Channeling emotional storytelling and social awareness through theatrical production and mime.",
                        "Annual drama festival, Street play awareness campaigns, Scriptwriting labs", "🎭"),

                // 10. Music & Acoustic Band Club
                new Club(0, "Music & Acoustic Band Club",
                        "Classical vocalists, Western instrumentalists, campus rock bands, and digital music production.",
                        "Cultural", "Prof. T. Swaminathan", 80,
                        "Fostering acoustic talent, vocal harmonies, band jamming sessions, and musical composition.",
                        "Campus unplugged nights, Band wars, Vocal training, Studio recording sessions", "🎵"),

                // 11. Fine Arts & Painting Club
                new Club(0, "Fine Arts & Painting Club",
                        "Sketching, acrylics, oil painting, digital illustration, sculpturing, and campus murals.",
                        "Creative Arts", "Dr. S. Revathi", 65,
                        "Promoting aesthetic visual expression and traditional fine arts mediums.",
                        "Annual art exhibition, Canvas painting marathons, Calligraphy workshops", "🎨"),

                // 12. Photography & Cinematography Club
                new Club(0, "Photography & Cinematography Club",
                        "Digital photography, portraiture, short filmmaking, post-processing, and drone cinematography.",
                        "Media", "Prof. G. Karthik", 75,
                        "Capturing campus life narratives through the lens and mastering visual storytelling techniques.",
                        "Photo walks, Short film competitions, Lightroom & Premiere editing masterclasses", "📷"),

                // 13. Dance & Choreography Club
                new Club(0, "Dance & Choreography Club",
                        "Classical Bharatanatyam, contemporary, hip-hop, western freestyle, and group formations.",
                        "Cultural", "Prof. M. Divya", 80,
                        "Expressing rhythm and dynamic choreography through solo and synchronized group dance.",
                        "Inter-collegiate dance fests, Choreography workshops, Flash mobs", "💃"),

                // 14. Sports & Athletics Association
                new Club(0, "Sports & Athletics Association",
                        "Football, basketball, cricket, badminton, track athletics, and tournament organizing.",
                        "Sports", "Dr. P. Selvan", 150,
                        "Instilling physical fitness, athletic discipline, sportsman spirit, and teamwork.",
                        "Inter-department leagues, Athletics championship, Fitness bootcamps", "⚽"),

                // 15. Yoga & Wellness Club
                new Club(0, "Yoga & Wellness Club",
                        "Hatha yoga, pranayama breathing, mindfulness meditation, posture alignment, and stress relief.",
                        "Health & Wellness", "Prof. H. Lakshmi", 90,
                        "Cultivating mental tranquility, ergonomic health, and holistic student wellness.",
                        "Sunrise yoga sessions, Guided meditation breaks, Exam stress-management workshops", "🧘"),

                // 16. National Service Scheme (NSS)
                new Club(0, "National Service Scheme (NSS)",
                        "Community empowerment, blood donation drives, literacy outreach, and rural village development camps.",
                        "Social Service", "Dr. C. Sundar", 150,
                        "Building social consciousness and public service dedication through active community engagement.",
                        "Annual special rural camp, Blood donation camps, Cleanliness drives (Swachh Bharat)", "🤝"),

                // 17. Youth Red Cross (YRC)
                new Club(0, "Youth Red Cross (YRC)",
                        "First aid certification, disaster management response, health awareness, and humanitarian assistance.",
                        "Social Service", "Dr. K. Anitha", 120,
                        "Equipping students with lifesaving first-responder skills and humanitarian service ethics.",
                        "First aid certified training, Disaster evacuation mock drills, Public health campaigns", "🏥"),

                // 18. Environmental & Nature Conservation Club
                new Club(0, "Environmental & Nature Conservation Club",
                        "Tree plantation drives, waste recycling audits, biodiversity conservation, and eco-sustainability.",
                        "Environmental", "Prof. D. Vasudevan", 80,
                        "Championing ecological sustainability and climate awareness on and off campus.",
                        "Campus green audits, Botanical tagging, Zero-plastic campaigns, Bird watching treks", "🌿"),

                // 19. Mathematics & Problem Solving Club
                new Club(0, "Mathematics & Problem Solving Club",
                        "Discrete mathematics, combinatorics, puzzle solving, cryptography, and quantitative aptitude.",
                        "Academic", "Dr. G. Srinivasan", 70,
                        "Sharpening abstract reasoning and mathematical analytical dexterity.",
                        "Math Olympiad training, Sudoku & Rubik's cube championships, Logic leagues", "📐"),

                // 20. Entrepreneurship & Innovation Cell (E-Cell)
                new Club(0, "Entrepreneurship & Innovation Cell (E-Cell)",
                        "Startup incubation, business model canvas, venture pitching, mentor networking, and seed grants.",
                        "Entrepreneurship", "Prof. R. Venkatesh", 100,
                        "Transforming student ideas into viable business ventures and cultivating startup leadership.",
                        "E-Summit, Pitch-to-Investors nights, Founder fireside chats, Lean startup bootcamps", "🚀"),

                // 21. Quantum Computing & Advanced Physics Club
                new Club(0, "Quantum Computing & Advanced Physics Club",
                        "Qubits, quantum algorithms, Qiskit circuits, quantum cryptography, and quantum entanglement.",
                        "Innovation", "Dr. A. Ramanathan", 50,
                        "Exploring next-generation quantum computing models and quantum information theory.",
                        "IBM Quantum circuit labs, Quantum algorithms seminars, Research paper circles", "⚛️"),

                // 22. Blockchain & Web3 Developers Guild
                new Club(0, "Blockchain & Web3 Developers Guild",
                        "Smart contracts, Solidity, decentralized finance (DeFi), Ethereum, and consensus protocols.",
                        "Innovation", "Prof. S. Vignesh", 75,
                        "Designing decentralized applications (dApps) and auditing EVM smart contract logic.",
                        "Solidity smart contract audits, Web3 hackathons, Tokenomics seminars", "⛓️"),

                // 23. AR/VR & Metaverse Experience Lab
                new Club(0, "AR/VR & Metaverse Experience Lab",
                        "Spatial computing, Unity 3D, Unreal Engine, Oculus headset development, and virtual simulations.",
                        "Innovation", "Dr. P. Malathi", 60,
                        "Pioneering immersive augmented and virtual reality simulations for training and education.",
                        "Unity XR game jams, Virtual campus tour creation, 3D spatial UI prototyping", "🥽"),

                // 24. Space Tech & Astronomy Guild
                new Club(0, "Space Tech & Astronomy Guild",
                        "Telescopic sky observation, satellite orbits, CanSat modeling, and astrophysics seminars.",
                        "Science", "Dr. K. Chandrasekar", 70,
                        "Nurturing astronomical observation curiosity and miniature space technology design.",
                        "Stargazing night camps, CanSat rocketry contests, Eclipse tracking sessions", "🔭"),

                // 25. Bio-Informatics & Genomics Research Club
                new Club(0, "Bio-Informatics & Genomics Research Club",
                        "Computational biology, DNA sequence alignment, AlphaFold protein structures, and Biopython.",
                        "Research", "Dr. U. Banumathi", 50,
                        "Interfacing computer science algorithms with life science genomic and proteomic datasets.",
                        "Biopython pipeline workshops, Protein docking sessions, Genomic data challenges", "🧬"),

                // 26. Game Design & Interactive Media Studio
                new Club(0, "Game Design & Interactive Media Studio",
                        "Indie game development, 2D/3D physics engines, character animation, and level mechanics.",
                        "Gaming", "Prof. J. Vijay", 80,
                        "Fostering complete video game production cycles from initial game design document to release.",
                        "48-hour Game Jams, Godot & Unreal asset creation, Game mechanics playtesting", "🎮"),

                // 27. Drone Technology & UAV Research Club
                new Club(0, "Drone Technology & UAV Research Club",
                        "Quadcopter assembly, flight controllers, autopilot navigation, FPV racing, and aerial survey.",
                        "Aerospace", "Dr. R. Natarajan", 60,
                        "Mastering unmanned aerial vehicle fabrication, PID tuning, and aerial mapping techniques.",
                        "Quadcopter assembly workshops, Autonomous waypoint navigation, Drone racing trials", "🚁"),

                // 28. Fintech & Algorithmic Trading Society
                new Club(0, "Fintech & Algorithmic Trading Society",
                        "Automated trading bots, backtesting quantitative models, portfolio risk, and payment gateways.",
                        "Finance", "Prof. S. Deepak", 65,
                        "Blending financial engineering with Python quantitative backtesting algorithms.",
                        "Paper trading leagues, Quant backtesting clinics, Algorithmic strategies seminars", "📈"),

                // 29. Social Media & Digital Content Creators Club
                new Club(0, "Social Media & Digital Content Creators Club",
                        "Podcasting, viral reels production, brand identity, audio editing, and audience analytics.",
                        "Media", "Prof. A. Sneha", 85,
                        "Empowering student creators with professional recording, editing, and content strategy skills.",
                        "Campus podcast series, Reel production challenges, Studio lighting workshops", "🎙️"),

                // 30. Model United Nations (MUN) & Geopolitics Society
                new Club(0, "Model United Nations (MUN) & Geopolitics Society",
                        "Diplomatic simulation, resolution drafting, UN committees, crisis debate, and foreign affairs.",
                        "Leadership", "Dr. F. George", 90,
                        "Cultivating multilateral diplomacy, negotiation, global affairs acumen, and crisis resolution.",
                        "Annual College Model UN, Crisis simulation councils, Treaty drafting workshops", "🌐")
            };

            for (Club c : defaultClubs) {
                if (clubDao.findByName(c.getName()).isEmpty()) {
                    clubDao.save(c);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice on seeding clubs: " + e.getMessage());
        }
    }
}
