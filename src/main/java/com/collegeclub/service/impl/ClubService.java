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
                // 1. ByteCraft — Coding & Open Source Society
                new Club(0, "ByteCraft — Coding & Open Source Society",
                        "Competitive programming, data structures, algorithms, open source contributions, and hackathon training.",
                        "Technical", "Dr. S. Arul", 120,
                        "Fostering coding proficiency, competitive programming skills, and open-source software collaboration.",
                        "Weekly DSA contests, Git workshops, Hackathons, LeetCode sprints", "💻"),

                // 2. RoboTech — AI, Robotics & Automation Club
                new Club(0, "RoboTech — AI, Robotics & Automation Club",
                        "Autonomous bots, ROS navigation, microcontrollers, embedded C, computer vision, and drone telemetry.",
                        "Technical", "Prof. K. Ramesh", 100,
                        "Empowering students to develop intelligent agents, deep learning models, and autonomous robotic prototypes.",
                        "Robotics bootcamps, Kaggle competitions, ROS hands-on labs, Bot-wars", "🤖"),

                // 3. NullSector — Cyber Security & Ethical Hacking Guild
                new Club(0, "NullSector — Cyber Security & Ethical Hacking Guild",
                        "Penetration testing, network defense, digital forensics, CTF challenges, and secure coding practices.",
                        "Technical", "Dr. V. Priya", 90,
                        "Building cyber defense capabilities and ethical hacking awareness across modern software architectures.",
                        "Capture The Flag (CTF) meets, Kali Linux workshops, Web Security drills", "🛡️"),

                // 4. DevForge — Full-Stack & App Development Club
                new Club(0, "DevForge — Full-Stack & App Development Club",
                        "Modern web architectures, React, Next.js, Flutter mobile development, cloud-native backends, and REST APIs.",
                        "Technical", "Prof. A. Kumar", 100,
                        "Training students in industry-standard responsive frontend and robust backend system design.",
                        "Full-stack project sprints, App design showcases, UI/UX bootcamps", "🚀"),

                // 5. CloudMatrix — Cloud Computing & DevOps Society
                new Club(0, "CloudMatrix — Cloud Computing & DevOps Society",
                        "Docker containerization, Kubernetes orchestration, CI/CD pipelines, AWS, Google Cloud, and site reliability.",
                        "Technical", "Dr. N. Sharma", 80,
                        "Mastering cloud infrastructure provisioning, Docker containers, and automated deployment architectures.",
                        "Docker crash courses, Kubernetes clusters setup, Cloud certification study groups", "☁️"),

                // 6. DataPulse — AI, ML & Big Data Society
                new Club(0, "DataPulse — AI, ML & Big Data Society",
                        "Exploratory analytics, statistical modeling, machine learning pipelines, BigQuery, Spark, and interactive dashboards.",
                        "Technical", "Dr. M. Jayakumar", 85,
                        "Transforming raw datasets into actionable intelligence through modern analytical pipelines.",
                        "Data visualization hackathons, Python pandas masterclasses, Big Data workshops", "📊"),

                // 7. CircuitStream — IoT & Embedded Systems Club
                new Club(0, "CircuitStream — IoT & Embedded Systems Club",
                        "Arduino, Raspberry Pi, sensor telemetry, smart automation prototypes, and industrial edge computing.",
                        "Technical", "Prof. R. Balaji", 75,
                        "Interfacing physical environments with smart microcontroller nodes and cloud telemetry.",
                        "Smart campus IoT buildathons, Embedded C firmware labs, Sensor interfacing clinics", "⚡"),

                // 8. Athena — Literary & Debating Society
                new Club(0, "Athena — Literary & Debating Society",
                        "Parliamentary debates, elocution, creative writing, poetry recitals, critical rhetoric, and annual literary fest.",
                        "Literary", "Dr. E. Elizabeth", 100,
                        "Developing articulation, critical thinking, public speaking prowess, and expressive writing.",
                        "Mock Parliamentary Debates, Creative writing symposiums, Oratory contests", "📚"),

                // 9. Abhinaya — Dramatics & Street Theatre Society
                new Club(0, "Abhinaya — Dramatics & Street Theatre Society",
                        "Proscenium stage plays, street theatre (nukkad natak), mime artistry, improv comedy, and voice modulation.",
                        "Cultural", "Prof. S. Manoharan", 70,
                        "Channeling emotional storytelling and social awareness through theatrical production and mime.",
                        "Annual drama festival, Street play awareness campaigns, Scriptwriting labs", "🎭"),

                // 10. Rhapsody — Music & Acoustic Band Club
                new Club(0, "Rhapsody — Music & Acoustic Band Club",
                        "Classical vocalists, Western instrumentalists, campus rock bands, keyboard fusion, and live acoustic jams.",
                        "Cultural", "Prof. T. Swaminathan", 80,
                        "Fostering acoustic talent, vocal harmonies, band jamming sessions, and musical composition.",
                        "Campus unplugged nights, Band wars, Vocal training, Studio recording sessions", "🎵"),

                // 11. Aura — Fine Arts, Painting & Design Club
                new Club(0, "Aura — Fine Arts, Painting & Design Club",
                        "Charcoal sketching, canvas acrylics, digital illustration, sculpturing, and campus mural exhibitions.",
                        "Creative Arts", "Dr. S. Revathi", 65,
                        "Promoting aesthetic visual expression and traditional fine arts mediums.",
                        "Annual art exhibition, Canvas painting marathons, Calligraphy workshops", "🎨"),

                // 12. ShutterBug — Photography & Cinematography Society
                new Club(0, "ShutterBug — Photography & Cinematography Society",
                        "Manual photography exposure, short filmmaking, campus photo walks, drone videography, and post-processing.",
                        "Media", "Prof. G. Karthik", 75,
                        "Capturing campus life narratives through the lens and mastering visual storytelling techniques.",
                        "Photo walks, Short film competitions, Lightroom & Premiere editing masterclasses", "📷"),

                // 13. Natyam — Dance & Choreography Ensemble
                new Club(0, "Natyam — Dance & Choreography Ensemble",
                        "Classical Bharatanatyam, contemporary lyrical, hip-hop street battles, and intra-college dance showcases.",
                        "Cultural", "Prof. M. Divya", 80,
                        "Expressing rhythm and dynamic choreography through solo and synchronized group dance.",
                        "Inter-collegiate dance fests, Choreography workshops, Flash mobs", "💃"),

                // 14. Titans — Sports & Athletics Association
                new Club(0, "Titans — Sports & Athletics Association",
                        "Inter-department football, basketball tournaments, cricket leagues, track athletics, and badminton championships.",
                        "Sports", "Dr. P. Selvan", 150,
                        "Instilling physical fitness, athletic discipline, sportsman spirit, and teamwork.",
                        "Inter-department leagues, Athletics championship, Fitness bootcamps", "⚽"),

                // 15. Zenith — Yoga & Mindfulness Wellness Club
                new Club(0, "Zenith — Yoga & Mindfulness Wellness Club",
                        "Hatha yoga asanas, guided pranayama breathing, mindful meditation, posture alignment, and wellness retreats.",
                        "Health & Wellness", "Prof. H. Lakshmi", 90,
                        "Cultivating mental tranquility, ergonomic health, and holistic student wellness.",
                        "Sunrise yoga sessions, Guided meditation breaks, Exam stress-management workshops", "🧘"),

                // 16. NSS — National Service Scheme Chapter
                new Club(0, "NSS — National Service Scheme Chapter",
                        "Community empowerment, blood donation drives, literacy outreach camps, and rural village development initiatives.",
                        "Social Service", "Dr. C. Sundar", 150,
                        "Building social consciousness and public service dedication through active community engagement.",
                        "Annual special rural camp, Blood donation camps, Cleanliness drives (Swachh Bharat)", "🤝"),

                // 17. RedCross — Youth Red Cross Society
                new Club(0, "RedCross — Youth Red Cross Society",
                        "Certified first aid training, emergency disaster response, health awareness, and humanitarian assistance.",
                        "Social Service", "Dr. K. Anitha", 120,
                        "Equipping students with lifesaving first-responder skills and humanitarian service ethics.",
                        "First aid certified training, Disaster evacuation mock drills, Public health campaigns", "🏥"),

                // 18. Prakriti — Nature & Environmental Conservation Club
                new Club(0, "Prakriti — Nature & Environmental Conservation Club",
                        "Tree plantation drives, e-waste audits, biodiversity conservation, rainwater harvesting, and eco-sustainability.",
                        "Environmental", "Prof. D. Vasudevan", 80,
                        "Championing ecological sustainability and climate awareness on and off campus.",
                        "Campus green audits, Botanical tagging, Zero-plastic campaigns, Bird watching treks", "🌿"),

                // 19. Infinity — Mathematics & Algorithmic Puzzles Club
                new Club(0, "Infinity — Mathematics & Algorithmic Puzzles Club",
                        "Discrete mathematics, combinatorics, cryptography puzzles, quantitative aptitude, and graph theory problem solving.",
                        "Academic", "Dr. G. Srinivasan", 70,
                        "Sharpening abstract reasoning and mathematical analytical dexterity.",
                        "Math Olympiad training, Sudoku & Rubik's cube championships, Logic leagues", "📐"),

                // 20. VentureLab — Entrepreneurship & Innovation Cell (E-Cell)
                new Club(0, "VentureLab — Entrepreneurship & Innovation Cell (E-Cell)",
                        "Startup incubation, business model canvas, venture pitch competitions, mentor networking, and seed funding support.",
                        "Entrepreneurship", "Prof. R. Venkatesh", 100,
                        "Transforming student ideas into viable business ventures and cultivating startup leadership.",
                        "E-Summit, Pitch-to-Investors nights, Founder fireside chats, Lean startup bootcamps", "🚀"),

                // 21. QuantumMatrix — Quantum Computing & Physics Guild
                new Club(0, "QuantumMatrix — Quantum Computing & Physics Guild",
                        "Qubits, quantum algorithm design, IBM Qiskit circuits, quantum cryptography, and entanglement principles.",
                        "Innovation", "Dr. A. Ramanathan", 50,
                        "Exploring next-generation quantum computing models and quantum information theory.",
                        "IBM Quantum circuit labs, Quantum algorithms seminars, Research paper circles", "⚛️"),

                // 22. ChainReaction — Blockchain & Web3 Developers Guild
                new Club(0, "ChainReaction — Blockchain & Web3 Developers Guild",
                        "Smart contracts, Solidity, decentralized finance (DeFi), Ethereum EVM architecture, and Web3 dApps.",
                        "Innovation", "Prof. S. Vignesh", 75,
                        "Designing decentralized applications (dApps) and auditing EVM smart contract logic.",
                        "Solidity smart contract audits, Web3 hackathons, Tokenomics seminars", "⛓️"),

                // 23. Vortex — AR/VR & Metaverse Experience Lab
                new Club(0, "Vortex — AR/VR & Metaverse Experience Lab",
                        "Spatial computing, Unity 3D, Unreal Engine, Oculus headset development, and virtual reality simulations.",
                        "Innovation", "Dr. P. Malathi", 60,
                        "Pioneering immersive augmented and virtual reality simulations for training and education.",
                        "Unity XR game jams, Virtual campus tour creation, 3D spatial UI prototyping", "🥽"),

                // 24. Cosmos — Space Tech & Astronomy Guild
                new Club(0, "Cosmos — Space Tech & Astronomy Guild",
                        "Telescopic stargazing, planetary imaging, CanSat nanosatellite design, and aerospace engineering seminars.",
                        "Science", "Dr. K. Chandrasekar", 70,
                        "Nurturing astronomical observation curiosity and miniature space technology design.",
                        "Stargazing night camps, CanSat rocketry contests, Eclipse tracking sessions", "🔭"),

                // 25. BioMatrix — Bioinformatics & Computational Biology Club
                new Club(0, "BioMatrix — Bioinformatics & Computational Biology Club",
                        "Genomic sequence alignment, BLAST queries, protein structure modeling with AlphaFold, and Biopython.",
                        "Research", "Dr. U. Banumathi", 50,
                        "Interfacing computer science algorithms with life science genomic and proteomic datasets.",
                        "Biopython pipeline workshops, Protein docking sessions, Genomic data challenges", "🧬"),

                // 26. PixelPlay — Game Design & Esports Guild
                new Club(0, "PixelPlay — Game Design & Esports Guild",
                        "Indie game development, 2D/3D physics engines, character animation, game jams, and college esports leagues.",
                        "Gaming", "Prof. J. Vijay", 80,
                        "Fostering complete video game production cycles from initial game design document to release.",
                        "48-hour Game Jams, Godot & Unreal asset creation, Game mechanics playtesting", "🎮"),

                // 27. AeroWings — Drone & UAV Research Club
                new Club(0, "AeroWings — Drone & UAV Research Club",
                        "Quadcopter assembly, flight controllers, autopilot navigation, FPV racing, and aerial survey robotics.",
                        "Aerospace", "Dr. R. Natarajan", 60,
                        "Mastering unmanned aerial vehicle fabrication, PID tuning, and aerial mapping techniques.",
                        "Quadcopter assembly workshops, Autonomous waypoint navigation, Drone racing trials", "🚁"),

                // 28. QuantEdge — Fintech & Algorithmic Trading Society
                new Club(0, "QuantEdge — Fintech & Algorithmic Trading Society",
                        "Automated trading bots, backtesting quantitative models in Python, risk management, and market analytics.",
                        "Finance", "Prof. S. Deepak", 65,
                        "Blending financial engineering with Python quantitative backtesting algorithms.",
                        "Paper trading leagues, Quant backtesting clinics, Algorithmic strategies seminars", "📈"),

                // 29. StudioCast — Podcast & Digital Media Creators Hub
                new Club(0, "StudioCast — Podcast & Digital Media Creators Hub",
                        "Studio podcasting, video production, brand identity, audio mixing, viral social storytelling, and creator workshops.",
                        "Media", "Prof. A. Sneha", 85,
                        "Empowering student creators with professional recording, editing, and content strategy skills.",
                        "Campus podcast series, Reel production challenges, Studio lighting workshops", "🎙️"),

                // 30. MUN Senate — Model United Nations & Geopolitics Society
                new Club(0, "MUN Senate — Model United Nations & Geopolitics Society",
                        "Diplomatic simulations, resolution drafting, UN committee procedures, crisis debate, and foreign affairs.",
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
