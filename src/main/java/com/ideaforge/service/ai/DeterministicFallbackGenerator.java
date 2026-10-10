package com.ideaforge.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DeterministicFallbackGenerator {

    private final ObjectMapper objectMapper;

    public DeterministicFallbackGenerator() {
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String generate(Idea idea) {
        IdeaMode mode = idea.getMode() != null ? idea.getMode() : IdeaMode.HACKATHON;
        Map<String, Object> blueprint = new LinkedHashMap<>();

        blueprint.put("mode", mode.name());
        blueprint.put("modeTitle", mode.getDisplayName());
        blueprint.put("ideaTitle", idea.getTitle());
        blueprint.put("tagline", idea.getTagline() != null ? idea.getTagline() : "");
        blueprint.put("generator", "DETERMINISTIC_FALLBACK_ENGINE");

        switch (mode) {
            case HACKATHON -> buildHackathonBlueprint(blueprint, idea);
            case COLLEGE_PROJECT -> buildCollegeProjectBlueprint(blueprint, idea);
            case STARTUP -> buildStartupBlueprint(blueprint, idea);
            case VALIDATE_IDEA -> buildValidateIdeaBlueprint(blueprint, idea);
            case OPEN_INNOVATION -> buildOpenInnovationBlueprint(blueprint, idea);
        }

        try {
            return objectMapper.writeValueAsString(blueprint);
        } catch (Exception e) {
            return "{\"error\": \"Failed to serialize deterministic blueprint\"}";
        }
    }

    private void buildHackathonBlueprint(Map<String, Object> bp, Idea idea) {
        bp.put("problemStatement", defaultIfBlank(idea.getProblemStatement(), "Modern teams lack unified tools for rapid problem execution."));
        bp.put("targetUsers", defaultIfBlank(idea.getTargetAudience(), "Hackathon participants, rapid prototypers, sprint teams"));
        bp.put("proposedSolution", defaultIfBlank(idea.getProposedSolution(), "A lightweight, modular workflow system for rapid delivery."));
        bp.put("uniqueValue", "Zero-setup workflow, deterministic feedback loops, and automated blueprint synthesis.");

        Map<String, Object> innovation = new LinkedHashMap<>();
        innovation.put("differentiation", "Focuses on rapid execution speed with verifiable milestone tracking instead of bloated enterprise suites.");
        innovation.put("mvpScope", List.of("Interactive idea configuration wizard", "Deterministic scoring breakdown", "Rapid experiment tracker"));
        bp.put("innovationAndDifferentiation", innovation);

        Map<String, Object> tech = new LinkedHashMap<>();
        tech.put("technicalArchitecture", "Decoupled Single Page Application (React) + Robust REST API (Spring Boot) + Relational Persistence (MySQL).");
        tech.put("technologyStack", defaultIfBlank(idea.getTechStack(), "React, Vite, Java 21, Spring Boot, MySQL, Spring Security JWT"));
        bp.put("technicalArchitecture", tech);

        bp.put("prototypeRoadmap", List.of(
                "Hour 0-4: Domain model, database schema migration, and JWT authentication setup",
                "Hour 4-12: Core entity CRUD, deterministic scoring engine, and similarity lookup",
                "Hour 12-20: Responsive React UI, real-time validations, and blueprint dashboard",
                "Hour 20-24: End-to-end integration tests, edge-case hardening, and demo rehearsal"
        ));

        bp.put("teamTaskSuggestions", List.of(
                "Frontend Lead: Build wizard stages, reactive score display, and experiment logger",
                "Backend Lead: Implement JWT filters, scoring formula, and MySQL JPA repositories",
                "QA / Demo Lead: Prepare live mock scenarios, slide deck, and 3-minute demo pitch"
        ));

        bp.put("risksAndExperiments", List.of(
                "Risk: API latency during live demo -> Mitigation: Use deterministic local cache and fallback adapters",
                "Risk: Time overrun on UI polish -> Mitigation: Use predefined clean CSS design system"
        ));

        bp.put("successMetrics", List.of("Under 2-minute project onboarding", "Sub-200ms API response latency", "Zero demo crashes"));

        bp.put("demoFlowAndPitchOutline", List.of(
                "1. Hook (30s): State the user pain point and current broken solutions",
                "2. Demo Walkthrough (90s): Live creation of an idea -> immediate blueprint & score -> feedback & experiment run",
                "3. Technical Wow (30s): Architecture robustness, security, and deterministic reproducibility",
                "4. Closing (30s): Impact summary and next development phases"
        ));
    }

    private void buildCollegeProjectBlueprint(Map<String, Object> bp, Idea idea) {
        bp.put("title", idea.getTitle());
        bp.put("abstractText", "This project introduces " + idea.getTitle() + ", an automated software framework addressing " +
                defaultIfBlank(idea.getProblemStatement(), "existing computational bottlenecks") + ". It utilizes " +
                defaultIfBlank(idea.getTechStack(), "modern software engineering patterns") + " to guarantee scalable, reliable outcomes.");
        bp.put("problemStatement", defaultIfBlank(idea.getProblemStatement(), "In traditional academic and operational environments, manual processes lead to delays, error rates, and lack of real-time validation."));
        bp.put("motivation", "Empowering engineering students and researchers with standardized validation methodologies.");
        bp.put("objectivesAndScope", List.of(
                "Design and deploy an end-to-end full-stack web application adhering to IEEE software design principles",
                "Implement role-based secure JWT access control and automated entity lifecycle management",
                "Develop algorithmic scoring and similarity detection modules with zero external telemetry dependence",
                "Validate system resilience with comprehensive unit and integration test suites"
        ));

        Map<String, Object> comparison = new LinkedHashMap<>();
        comparison.put("existingSystems", "Manual spreadsheets, fragmented forum discussions, and proprietary monolithic platforms with high latency and vendor lock-in.");
        comparison.put("proposedSystem", "Decoupled 3-tier architecture with transactional MySQL persistence, reactive SPA frontend, and transparent scoring metrics.");
        bp.put("systemComparison", comparison);

        Map<String, Object> srs = new LinkedHashMap<>();
        srs.put("functionalRequirements", List.of(
                "FR-01: User registration and tokenized authentication with BCrypt hashing",
                "FR-02: Multi-mode idea lifecycle management (Hackathon, College Project, Startup, Validate, Open Innovation)",
                "FR-03: Algorithmic assessment generation with criterion weight breakdowns",
                "FR-04: Peer feedback submission and hypothesis-driven experiment tracking"
        ));
        srs.put("nonFunctionalRequirements", List.of(
                "NFR-01: Response time < 500ms under standard local network concurrency",
                "NFR-02: Strict adherence to ACID transactional compliance via JPA/Hibernate",
                "NFR-03: Responsive presentation conforming to WCAG contrast standards"
        ));
        bp.put("softwareRequirementsSpecification", srs);

        bp.put("moduleBreakdown", List.of(
                "Module 1: Authentication & Identity Management (Spring Security, JWT)",
                "Module 2: Core Domain Engine (Ideas, Modes, Blueprints, JPA)",
                "Module 3: Deterministic Scoring & Similarity Discovery (Multi-factor weighting, Jaccard tokenization)",
                "Module 4: Feedback & Experiment Validation Tracker (Peer review, hypothesis testing)",
                "Module 5: Presentation & Visualization (React, Vite, CSS design tokens)"
        ));

        bp.put("systemArchitectureAndDatabaseDesign", List.of(
                "Architecture: MVC + Service-Repository Layered Architecture with stateless REST API",
                "Database Entities: users (1:N) ideas (1:N) assessments, feedbacks, experiments",
                "Referential Integrity: Foreign keys with ON DELETE CASCADE / RESTRICT constraints"
        ));

        bp.put("semesterMilestones", List.of(
                "Milestone 1: Literature survey, SRS documentation, and architecture diagramming",
                "Milestone 2: Database schema normalization and core REST API implementation",
                "Milestone 3: UI component hierarchy, Axios integration, and client-side routing",
                "Milestone 4: Unit testing, integration verification, and viva presentation documentation"
        ));

        bp.put("testingPlan", List.of(
                "Unit Testing: JUnit 5 & Mockito for service layer logic and boundary validation",
                "Integration Testing: MockMvc for Spring MVC endpoint security and payload verification",
                "User Acceptance Testing: End-to-end workflow verification across student user stories"
        ));

        bp.put("presentationOutline", List.of(
                "Slide 1-2: Introduction, Problem Statement & Motivation",
                "Slide 3-4: Literature Review & Limitations of Existing Systems",
                "Slide 5-6: Proposed Architecture, SRS, and Database Schema",
                "Slide 7-9: Live System Demonstration & Test Execution Results",
                "Slide 10: Conclusion, Future Scope & References"
        ));
    }

    private void buildStartupBlueprint(Map<String, Object> bp, Idea idea) {
        bp.put("targetCustomers", defaultIfBlank(idea.getTargetAudience(), "Early-stage innovators, university entrepreneurs, and incubation teams."));
        bp.put("painPoints", defaultIfBlank(idea.getProblemStatement(), "Entrepreneurs spend months building products without early customer problem validation."));
        bp.put("valueProposition", defaultIfBlank(idea.getProposedSolution(), "Structured idea validation and rapid customer feedback loops before writing code."));
        bp.put("mvpDefinition", "Core 5-step validation workflow: Idea onboarding -> Blueprint -> Deterministic scoring -> Peer feedback -> Experiment recording.");

        Map<String, Object> businessModel = new LinkedHashMap<>();
        businessModel.put("modelType", "Freemium SaaS for individuals; B2B Campus License for universities & incubators.");
        businessModel.put("potentialRevenueSources", List.of(
                "Tier 1: Free tier for individual students and solo builders",
                "Tier 2: Premium Pro subscription for advanced AI integrations, PDF exports, and team collaboration",
                "Tier 3: Annual university institutional licenses for campus entrepreneurship cells"
        ));
        businessModel.put("costStructure", List.of(
                "Cloud infrastructure and managed MySQL database hosting",
                "AI inference API token consumption (budgeted per tier)",
                "Domain, SSL, and operational maintenance overhead"
        ));
        bp.put("businessModelAndMonetization", businessModel);

        bp.put("competitionAssumptions", List.of(
                "Assumption: Competitors focus on generic project management (e.g., Trello, Jira) or vague brainstorming notes (e.g., Miro).",
                "Differentiation: IdeaForge provides opinionated, mode-tailored engineering frameworks and deterministic validation scoring."
        ));

        bp.put("goToMarketApproach", List.of(
                "Phase 1: Direct grassroots adoption in university hackathons and student developer clubs",
                "Phase 2: Open-source academic edition to drive developer community goodwill and organic inbound interest",
                "Phase 3: Formal partnership pitches to university incubation centers and startup accelerators"
        ));

        bp.put("validationMetricsAndKPIs", List.of(
                "Customer Acquisition Cost (CAC) vs Lifetime Value (LTV) target ratio > 3:1",
                "Weekly Active Validations (WAV): Ideas completing >= 2 experiments per month",
                "Net Promoter Score (NPS) > 50 among student entrepreneurs"
        ));

        bp.put("operationalRisks", List.of(
                "Risk: High churn if users abandon ideas early -> Mitigation: Automated experiment reminders and progression badges",
                "Risk: Over-reliance on third-party LLMs -> Mitigation: Dual-engine architecture with instant deterministic fallback"
        ));
    }

    private void buildValidateIdeaBlueprint(Map<String, Object> bp, Idea idea) {
        bp.put("coreAssumptions", List.of(
                "Assumption 1 (Problem): The stated problem causes tangible friction and is actively sought to be solved.",
                "Assumption 2 (Willingness): Target users will adopt a dedicated digital platform rather than continuing with ad-hoc manual habits.",
                "Assumption 3 (Feasibility): The proposed technical stack can be deployed within allocated resource and time constraints."
        ));

        bp.put("feasibilityAndConstraints", List.of(
                "Technical Feasibility: High. Uses industry-standard Spring Boot and React stack with proven MySQL reliability.",
                "Resource Constraints: Single-developer or small team maintenance overhead must remain low.",
                "Security & Privacy Constraints: User idea intellectual property must remain isolated with strict ownership authorization."
        ));

        bp.put("riskAndUncertaintiesMatrix", List.of(
                "Desirability Risk: Target audience might perceive initial setup as time-consuming.",
                "Viability Risk: University adoption cycles may require departmental approvals.",
                "Feasibility Risk: AI quota depletion or rate limiting under burst load."
        ));

        bp.put("userResearchQuestions", List.of(
                "1. How do you currently document, evaluate, and track your project concepts?",
                "2. What is the single biggest hurdle you face when preparing your project for faculty review or hackathon judges?",
                "3. Which metric would convince you that an idea is ready for full prototype development?"
        ));

        bp.put("experimentPlanAndProtocols", List.of(
                "Experiment 1: Conduct 10 structured interviews with target users using the 3 research questions.",
                "Experiment 2: Run a pilot cohort with 3 hackathon teams tracking their project development using IdeaForge.",
                "Experiment 3: Measure completion rate of the 5-stage validation workflow."
        ));

        bp.put("successCriteriaAndEvidenceRules", List.of(
                "Criteria: >= 70% of interviewed users report actionable clarity from the generated blueprint.",
                "Evidence: Minimum 3 logged experiment reports with documented quantitative results and lesson takeaways."
        ));

        bp.put("actionableRecommendations", List.of(
                "Do not write complex microservices before validating the core single-page workflow.",
                "Focus on crisp, testable hypotheses instead of subjective feature requests.",
                "Reassess idea score after every 2 completed experiments to track score trajectory."
        ));
    }

    private void buildOpenInnovationBlueprint(Map<String, Object> bp, Idea idea) {
        bp.put("problemDefinition", defaultIfBlank(idea.getProblemStatement(), "Complex modern engineering challenges require cross-disciplinary collaboration and open ecosystem participation."));
        bp.put("alternativeSolutions", List.of(
                "Alternative A: Closed proprietary development within isolated corporate silos (High cost, slow feedback).",
                "Alternative B: Ad-hoc community forums without structured evaluation or validation benchmarks (Unfocused, high noise).",
                "Alternative C (Selected): Open innovation framework combining public ideation with verifiable validation experiments."
        ));

        bp.put("potentialImpactAndScale", List.of(
                "Direct beneficiaries: Open-source contributors, student researchers, and community lab organizers.",
                "Systemic impact: Democratizes access to high-standard software project specifications and architectural patterns."
        ));

        Map<String, Object> tradeOffs = new LinkedHashMap<>();
        tradeOffs.put("strengths", "High transparency, rapid crowd-sourced feedback, diverse perspectives, and distributed testing.");
        tradeOffs.put("weaknessesAndTradeOffs", "Risk of IP leakage if ideas are public; requires community moderation to maintain feedback quality.");
        tradeOffs.put("mitigation", "Provide granular visibility controls and peer review rating verification.");
        bp.put("tradeOffAnalysis", tradeOffs);

        bp.put("resourceRequirements", List.of(
                "Infrastructure: Standard web host with containerized Spring Boot runtime and persistent MySQL storage.",
                "Human Resources: Maintainer for PR reviews, community moderators, and domain expert mentors.",
                "Ecosystem: GitHub repository, developer Discord/Slack, and clear contribution guidelines."
        ));

        bp.put("ecosystemCollaborationPlan", List.of(
                "Stage 1: Publish standardized problem statement and call for community experiment proposals",
                "Stage 2: Facilitate cross-institutional feedback rounds categorized by Strengths, Concerns, Feasibility, and Suggestions",
                "Stage 3: Consolidate empirical experiment evidence and open-source the verified prototype"
        ));
    }

    private String defaultIfBlank(String value, String fallback) {
        return (value != null && !value.isBlank()) ? value.trim() : fallback;
    }
}
