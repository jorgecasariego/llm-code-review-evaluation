# LLM Code Review Evaluation

A small evaluation comparing how different Large Language Models review Android/Kotlin implementations when the available requirements and code are intentionally limited.

The main goal of the experiment is not only to evaluate technical correctness, but also to measure whether a model can distinguish between:

- what is explicitly supported by the provided code;
- what cannot be determined from the available information;
- and what would require making an unsupported assumption.

## Models Evaluated

| Model | Configuration | Date Tested |
|---|---|---|
| ChatGPT | Instant | September 3, 2026 |
| Claude | Sonnet 5 Medium | September 3, 2026 |

## Evaluation Setup

Each model received the same Android/Kotlin code-review cases.

The prompts were intentionally designed to test whether the model would:

- identify requirements that are not correctly implemented;
- recognize when the available information is insufficient;
- avoid inventing missing architecture or implementation details;
- avoid introducing requirements that are not present in the ticket;
- provide technically correct and relevant recommendations.

## Test Cases

The evaluation contains five cases covering different forms of incomplete or constrained information.

### Case 1 — Unknown Retry Behavior

Tests whether the model invents a retry count or delay when the implementation of the repository and networking layer is unavailable.

### Case 2 — Missing Empty State

Tests whether the model identifies a requirement that is genuinely missing without expanding the review into unrelated improvements.

### Case 3 — Incomplete Implementation

Tests how the model responds when the implementation contains only a `TODO` and there is insufficient information to validate the requirements.

### Case 4 — Abstraction Boundaries

Tests how the model reasons about behavior delegated to another composable and whether it introduces additional requirements that are not part of the ticket.

### Case 5 — Insufficient Retry Information

A second retry-related case designed to verify whether the model consistently refuses to infer behavior hidden behind a repository abstraction.

## Evaluation Criteria

Each response was scored from **1 to 5** across five dimensions:

1. **Correctness**
2. **Groundedness**
3. **Requirement Adherence**
4. **Unsupported Assumptions**
5. **Conciseness and Relevance**

Each case has a maximum score of **25 points**, for a maximum overall score of **125 points per model**.

## Results

| Model | Score | Percentage |
|---|---:|---:|
| ChatGPT Instant | **125/125** | **100%** |
| Claude Sonnet 5 Medium | **112/125** | **89.6%** |

## Key Finding

Both models demonstrated strong Android/Kotlin technical knowledge and generally handled uncertainty well.

The main difference was **scope discipline**.

ChatGPT Instant consistently stayed within the boundaries of the provided ticket and implementation, explicitly stating when information could not be determined without introducing unrelated recommendations.

Claude Sonnet 5 Medium also demonstrated strong grounding and uncertainty awareness, but occasionally expanded the review with technically reasonable observations that were not supported by the ticket, such as additional layout or `LazyColumn` recommendations.

This experiment highlights an important distinction:

> A technically valid observation is not necessarily a grounded answer to the question being asked.

For constrained code-review tasks, strong performance requires not only technical knowledge, but also the ability to recognize **when not to infer, when not to expand the scope, and when the available evidence is simply insufficient**.

## Repository Contents

The repository contains the complete experiment, including:

- test cases and prompts;
- raw outputs from each model;
- evaluation criteria;
- case-by-case scoring;
- final comparison and observations.

## Purpose

This repository is intended as a small practical experiment in evaluating LLM behavior for software-engineering tasks, particularly around:

- grounded reasoning;
- code review;
- requirement validation;
- uncertainty handling;
- hallucination and unsupported assumptions.