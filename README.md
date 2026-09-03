# Taller Generative AI Foundations

Hands-on exercises and experiments completed as part of the Taller Generative AI Foundations course.

This repository documents my practical exploration of generative AI fundamentals, including identifying appropriate uses for generative models, evaluating risk and human oversight, understanding tokenization and cost, designing evaluation sets, and comparing model behavior through controlled experiments.

Rather than only recording final answers, the labs include predictions, measurements, model outputs, evaluation criteria, comparisons, failures, and observations used to understand how generative AI systems behave in practical software-engineering scenarios.

---

## Course Progress

### Day 1 — Generative AI Foundations

Topics covered:

- Generative AI task fit
- Good fit, risky fit, and poor fit
- Human review and verification
- Token estimation
- Tokenizer behavior
- Token measurement
- Input cost estimation
- Model evaluation
- Test-set design
- Success criteria
- Groundedness
- Unsupported assumptions
- Controlled model comparison

Lab: [Day 1 — Generative AI Foundations](day-1/day-1-lab.md)

#### Experiments

The Day 1 lab explores several aspects of working with generative AI through practical exercises:

- Classifying real software-engineering tasks as good, risky, or poor fits for generative AI
- Defining verification steps for risky AI-assisted tasks
- Estimating token counts before measuring them
- Comparing token estimates with actual tokenizer results
- Comparing tokenization behavior between English technical prose and Kotlin source code
- Observing how different tokenizers can produce significantly different token counts for the same input
- Estimating the monthly input cost of using a model at realistic request volume
- Designing a five-case evaluation set before running the models
- Defining checkable success criteria for every evaluation case
- Comparing ChatGPT Instant and Claude Sonnet 5 Medium on identical Android/Kotlin code-review tasks
- Testing ordinary, edge, ambiguous, and intentionally unanswerable cases
- Evaluating correctness, groundedness, requirement adherence, unsupported assumptions, and relevance

#### Model Evaluation

The controlled comparison used five Android/Kotlin code-review cases designed to test whether a model could distinguish between:

- requirements that are clearly satisfied;
- requirements that are clearly missing;
- behavior that cannot be verified from the provided implementation;
- information that cannot be determined without additional code;
- technically plausible observations that are nevertheless outside the requested scope.

Each response was scored across five dimensions:

1. Correctness
2. Groundedness
3. Requirement Adherence
4. Unsupported Assumptions
5. Conciseness and Relevance

Final results:

| Model | Score | Percentage |
|---|---:|---:|
| ChatGPT Instant | **125/125** | **100%** |
| Claude Sonnet 5 Medium | **107/125** | **85.6%** |

Both models demonstrated strong Android/Kotlin technical knowledge. The main difference observed in this experiment was **scope discipline**: ChatGPT Instant stayed more consistently within the evidence and explicit requirements, while Claude Sonnet 5 Medium more frequently introduced technically reasonable observations beyond the scope of the ticket.

#### Key takeaway

A technically valid answer is not necessarily a well-grounded answer.

Working effectively with generative AI requires defining what success means before evaluating the model, distinguishing between what the available evidence supports and what would require assumptions, and recognizing that model quality depends not only on technical knowledge but also on knowing when **not** to infer.

The lab also reinforced that token estimates should be treated as planning approximations rather than exact conversions, and that model evaluation should be based on observed behavior rather than assumptions about how a model is expected to perform.

---

## Repository Structure

```text
taller-generative-ai-foundations/
├── README.md
└── day-1/
    ├── day-1-lab.md
    └── samples/
        ├── android-private-key-article.txt
        └── android-source-code.kt
```

---

## Approach

The exercises in this repository follow an experimental approach:

1. Identify the task and determine whether generative AI is an appropriate tool.
2. Define the expected behavior or success criteria before running the model.
3. Record predictions before measuring outcomes when applicable.
4. Preserve the actual model outputs.
5. Compare results against predefined criteria.
6. Distinguish observations from unsupported assumptions.
7. Document both successful and unsuccessful outcomes.
8. Prefer conclusions supported by observed evidence rather than expected model behavior.

This means that an unexpected result is still useful evidence.

For example, the token-estimation experiment initially predicted that the four-characters-per-token rule would perform worse for Kotlin source code than for English technical prose. The measured results showed the opposite for the tokenizer used in the experiment. Rather than changing the prediction after seeing the result, the discrepancy was documented and used as part of the analysis.

The same principle applies to model evaluation: a technically sophisticated response is not automatically better if it introduces assumptions or recommendations that are not supported by the task.

---

## Tools and Topics

The repository currently explores:

`Generative AI` · `LLMs` · `AI Evaluation` · `Model Comparison` · `Groundedness` · `Hallucinations` · `Unsupported Assumptions` · `Human-in-the-loop` · `Tokenization` · `Token Estimation` · `Cost Estimation` · `Test-set Design` · `Success Criteria` · `Android` · `Kotlin` · `Jetpack Compose` · `Code Review` · `Requirement Validation`

---

## About

Created as part of my ongoing exploration of AI-assisted software engineering and practical techniques for evaluating and using large language models effectively.