package com.ideaforge.service;

import com.ideaforge.dto.SimilarityMatchResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.repository.IdeaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SimilarityService {

    private final IdeaRepository ideaRepository;

    private static final Set<String> STOPWORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "can", "did", "do", "does", "doing", "don", "down", "during", "each", "few", "for",
            "from", "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself",
            "him", "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just",
            "me", "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once",
            "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "s", "same", "she",
            "should", "so", "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves",
            "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until",
            "up", "very", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom",
            "why", "will", "with", "you", "your", "yours", "yourself", "yourselves", "system", "app", "platform"
    );

    private static final Pattern WORD_PATTERN = Pattern.compile("[^a-zA-Z0-9]+");

    public SimilarityService(IdeaRepository ideaRepository) {
        this.ideaRepository = ideaRepository;
    }

    public List<SimilarityMatchResponse> findSimilarIdeas(Long ideaId, Integer minThreshold) {
        int threshold = minThreshold != null ? minThreshold : 15;
        Idea target = ideaRepository.findById(ideaId).orElse(null);
        if (target == null) {
            return Collections.emptyList();
        }

        List<Idea> candidates = ideaRepository.findAllExceptId(ideaId);
        Set<String> targetTokens = extractKeywords(target);
        Set<String> targetTags = extractTagTokens(target.getTags());

        List<SimilarityMatchResponse> matches = new ArrayList<>();

        for (Idea candidate : candidates) {
            Set<String> candTokens = extractKeywords(candidate);
            Set<String> candTags = extractTagTokens(candidate.getTags());

            // 1. Tag overlap
            Set<String> commonTags = new HashSet<>(targetTags);
            commonTags.retainAll(candTags);

            // 2. Keyword overlap
            Set<String> commonTokens = new HashSet<>(targetTokens);
            commonTokens.retainAll(candTokens);

            if (commonTokens.isEmpty() && commonTags.isEmpty()) {
                continue;
            }

            // Jaccard similarity for keywords
            Set<String> union = new HashSet<>(targetTokens);
            union.addAll(candTokens);
            double jaccard = union.isEmpty() ? 0.0 : (double) commonTokens.size() / union.size();

            // Weighted score: 60% keyword overlap, 25% tag overlap bonus, 15% same mode bonus
            double score = jaccard * 100.0 * 1.5; // Scale Jaccard
            if (!commonTags.isEmpty()) {
                score += Math.min(25, commonTags.size() * 12);
            }
            if (target.getMode() == candidate.getMode()) {
                score += 15;
            }

            int finalPercentage = (int) Math.min(100, Math.round(score));

            if (finalPercentage >= threshold) {
                List<String> matchedWords = new ArrayList<>();
                matchedWords.addAll(commonTags);
                for (String t : commonTokens) {
                    if (!matchedWords.contains(t) && matchedWords.size() < 6) {
                        matchedWords.add(t);
                    }
                }

                StringBuilder reason = new StringBuilder();
                if (target.getMode() == candidate.getMode()) {
                    reason.append("Shares the same mode (").append(target.getMode().getDisplayName()).append("). ");
                }
                if (!commonTags.isEmpty()) {
                    reason.append("Overlapping tags: ").append(String.join(", ", commonTags)).append(". ");
                }
                if (!commonTokens.isEmpty()) {
                    reason.append("Common keywords: ").append(String.join(", ", commonTokens.stream().limit(4).collect(Collectors.toList()))).append(".");
                }

                String ownerName = candidate.getUser() != null ? candidate.getUser().getName() : "Anonymous";
                Long ownerId = candidate.getUser() != null ? candidate.getUser().getId() : null;

                matches.add(new SimilarityMatchResponse(
                        candidate.getId(),
                        candidate.getTitle(),
                        candidate.getTagline(),
                        candidate.getMode(),
                        candidate.getCurrentStage(),
                        finalPercentage,
                        matchedWords,
                        reason.toString().trim(),
                        ownerId,
                        ownerName
                ));
            }
        }

        // Sort descending by match percentage
        matches.sort((a, b) -> Integer.compare(b.getMatchPercentage(), a.getMatchPercentage()));
        return matches;
    }

    private Set<String> extractKeywords(Idea idea) {
        StringBuilder sb = new StringBuilder();
        if (idea.getTitle() != null) sb.append(idea.getTitle()).append(" ");
        if (idea.getTagline() != null) sb.append(idea.getTagline()).append(" ");
        if (idea.getProblemStatement() != null) sb.append(idea.getProblemStatement()).append(" ");
        if (idea.getProposedSolution() != null) sb.append(idea.getProposedSolution()).append(" ");
        if (idea.getTechStack() != null) sb.append(idea.getTechStack()).append(" ");
        if (idea.getDescription() != null) sb.append(idea.getDescription()).append(" ");

        return tokenize(sb.toString());
    }

    private Set<String> extractTagTokens(String tags) {
        if (tags == null || tags.isBlank()) return Collections.emptySet();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty() && !STOPWORDS.contains(s))
                .collect(Collectors.toSet());
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        String[] words = WORD_PATTERN.split(text.toLowerCase());
        Set<String> set = new HashSet<>();
        for (String w : words) {
            if (w.length() >= 3 && !STOPWORDS.contains(w)) {
                set.add(w);
            }
        }
        return set;
    }
}
