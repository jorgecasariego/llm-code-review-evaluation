# Taller Generative AI Foundations

Hands-on exercises and experiments completed as part of the Taller Generative AI Foundations course.

This repository documents my practical exploration of generative AI fundamentals, including identifying appropriate uses for generative models, evaluating risk and human oversight, understanding tokenization and cost, designing evaluation sets, managing conversation state, building grounded RAG flows, and controlling tool execution.

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

### Day 2 — Building LLM Applications

Topics covered:

- Prompt iteration and output control
- Roles, audience, structure, and length constraints
- Prompt injection and trust boundaries
- Defensive single-shot flows
- Input and output validation
- Stop-reason handling
- Conversation state
- Trimming vs. summarization
- Persistent system instructions
- Retrieval-Augmented Generation (RAG)
- Chunking and retrieval behavior
- Retrieval misses vs. knowledge gaps
- Grounded answers and citation verification
- Tool / function calling
- Application-side tool validation
- Tool authorization and destructive actions
- Bounded tool loops

Lab: [Day 2 — Build the Four Patterns](day-2/day-2-lab.md)

#### Experiments

The Day 2 lab builds progressively from prompt design to application-level control around an LLM.

The experiments include:

- Iterating an Android/Kotlin code-review prompt one variable at a time
- Measuring how role, audience, output structure, length limits, and examples change model behavior
- Testing a prompt-injection attempt embedded inside untrusted code-review content
- Making trust boundaries explicit using delimiters and defensive instructions
- Designing a defensive single-shot flow with input validation, output caps, stop-reason handling, and structural output validation
- Simulating a truncated response and rejecting it based on `max_tokens`
- Running an eight-turn Android architecture conversation with a persistent behavioral instruction
- Comparing conversation trimming with summarization
- Demonstrating how trimming can lose important facts while summarization can preserve them with less context
- Building a fictional RAG corpus and evaluating successful retrieval, insufficient evidence, and retrieval failure
- Verifying whether generated citations actually support model claims
- Distinguishing retrieval failures from generation failures
- Simulating a complete tool-calling flow using a read-only order-status tool
- Rejecting invalid model-generated tool arguments
- Testing an unsupported action without allowing the model to invent capabilities
- Designing application-side authorization and bounded tool loops

#### RAG Evaluation

The RAG experiment used a fictional internal knowledge base called **Nebula** and tested three distinct cases:

| Test | Retrieval | Answer in corpus | Expected behavior |
|---|---|---|---|
| Answer present | Correct chunk retrieved | Yes | Grounded answer |
| Information absent | Relevant chunk retrieved | No | Correct refusal |
| Retrieval miss | No chunk retrieved | Yes | Retrieval failure |

The experiment demonstrated that an unsatisfactory RAG answer is not automatically a model-generation failure.

A system must first determine whether the correct evidence was retrieved before evaluating how the model used that evidence.

It also demonstrated that citations can make an answer appear more trustworthy without guaranteeing that the cited source actually supports the claim.

#### Tool Calling

The tool-calling experiment used a small read-only function:

```text
get_order_status(orderId)
```

The complete flow was:

```text
User request
      ↓
Model requests tool
      ↓
Application validates request
      ↓
Application executes tool
      ↓
Structured tool result
      ↓
Model produces final response
```

Additional tests demonstrated that:

- model-generated arguments must be treated as untrusted input;
- unsupported tools must not be invented;
- destructive operations require application-side authorization and potentially explicit human confirmation;
- tool execution failures must be represented explicitly;
- tool-call loops should have application-defined limits.

#### Key takeaway

The central lesson from Day 2 is that reliability does not come from prompting alone.

```text
LLM output is probabilistic
          ↓
Application adds control
          ↓
Validate input
Control context
Inspect stop reason
Validate output
Verify retrieved evidence
Validate tool calls
Enforce authorization
Bound execution
          ↓
Reliable application behavior
```

The model can generate, reason, retrieve through provided context, or request actions, but the application remains responsible for validation, state, authorization, and control.

---

## Repository Structure

```text
taller-generative-ai-foundations/
├── README.md
├── day-1/
│   ├── day-1-lab.md
│   └── samples/
│       ├── android-private-key-article.txt
│       └── android-source-code.kt
└── day-2/
    └── day-2-lab.md
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
7. Deliberately test failure modes and edge cases.
8. Separate model responsibilities from application responsibilities.
9. Document both successful and unsuccessful outcomes.
10. Prefer conclusions supported by observed evidence rather than expected model behavior.

This means that an unexpected result is still useful evidence.

For example, the token-estimation experiment initially predicted that the four-characters-per-token rule would perform worse for Kotlin source code than for English technical prose. The measured results showed the opposite for the tokenizer used in the experiment. Rather than changing the prediction after seeing the result, the discrepancy was documented and used as part of the analysis.

The same principle applies to LLM application design: a technically sophisticated response is not automatically reliable if it introduces unsupported assumptions, receives incorrect retrieval context, ignores application constraints, or attempts an action that has not been validated.

---

## Tools and Topics

The repository currently explores:

`Generative AI` · `LLMs` · `Prompt Engineering` · `Prompt Injection` · `Trust Boundaries` · `AI Evaluation` · `Model Comparison` · `Groundedness` · `Hallucinations` · `Unsupported Assumptions` · `Human-in-the-loop` · `Tokenization` · `Token Estimation` · `Cost Estimation` · `Test-set Design` · `Success Criteria` · `Conversation State` · `Context Management` · `Summarization` · `RAG` · `Retrieval` · `Chunking` · `Citation Verification` · `Tool Calling` · `Function Calling` · `Authorization` · `Input Validation` · `Output Validation` · `Android` · `Kotlin` · `Jetpack Compose` · `Code Review` · `Requirement Validation`

---

## About

Created as part of my ongoing exploration of AI-assisted software engineering and practical techniques for building, evaluating, and controlling applications powered by large language models.