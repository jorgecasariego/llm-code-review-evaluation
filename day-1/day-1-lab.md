## Exercise 1 — Map tasks to the right use of generative AI

### Task 1 - Review Android/Kotlin code before submitting a pull request
**Task:** Review Android/Kotlin code and suggest possible improvements before submitting a pull request.
**Generative role:** Draft
**Fit:** Risky fit
**Reason:** The model can draft useful code-review suggestions, but a recommendation that looks correct and idiomatic could introduce a lifecycle, concurrency, or state-management bug that only appears in an edge case.
**Check:** A developer must review the suggested changes and validate them with the existing tests, or add tests for the affected behavior, before applying them.


### Task 2 — Summarize technical documentation for study

**Task:** Summarize a long technical document into concise study notes.

**Generative role:** Summary

**Fit:** Good fit

**Reason:** The summary is used as a study aid rather than as an authoritative source. If the model omits or misrepresents a detail, I can verify important information against the original document before relying on it.


### Task 3 — Generate Android code from a specification or ticket

**Task:** Generate an initial Android implementation from a product specification or engineering ticket.

**Generative role:** Draft

**Fit:** Risky fit

**Reason:** The generated implementation may compile and look correct while silently making assumptions about requirements that the ticket does not specify, such as error handling, concurrency, lifecycle behavior, or edge cases. Those assumptions could produce incorrect production behavior.

**Check:** An engineer must compare the implementation against the acceptance criteria, review the generated code, and test the expected behavior and relevant edge cases before merging it.


### Task 4 — Accept AI-generated code without human review

**Task:** Accept an AI-generated implementation as production-ready code without reviewing it.

**Generative role:** None

**Fit:** Poor fit

**Reason:** This is a final engineering decision rather than a draft, summary, or transformation. AI-generated code can compile and look correct while still containing subtle bugs or incorrect assumptions about requirements and edge cases. Without human review, those problems could reach production unnoticed.


### Task 5 — Generate a commit message from branch changes

**Task:** Generate a commit message after reviewing the code changes made in my branch.

**Generative role:** Draft

**Fit:** Good fit

**Reason:** The model can draft a concise commit message from the actual code changes. If it misinterprets a change, the main risk is an inaccurate or misleading commit history, and I can easily catch this by reviewing the message before committing.


## Exercise 2 — Token and cost budget

### Part A — Estimate, then measure

#### Sample 1 — English technical prose

**Source:** "Where Should an Android App Store a Private Key?"  
**Text type:** English technical article  
**Character count:** 4,055 characters  
**Rule-of-thumb:** ~4 characters per token  
**Estimated token count:** 4,055 / 4 = **1,013.75 ≈ 1,014 tokens**

I recorded this estimate before measuring the actual token count.

**Measured token count:** 798 tokens  
**Tokenizer used:** OpenAI Tokenizer — GPT-5.x & O1/O3 tokenizer

**Difference:** 1,014 - 798 = 216 tokens

**Estimation error:** (216 / 798) × 100 = **27.1% overestimate**

The four-characters-per-token rule overestimated the measured token count by approximately 27.1%. For this sample, the actual ratio was approximately 5.08 characters per token (4,055 / 798).

### Part B — Where the rule of thumb breaks

#### Sample 2 — Kotlin source code

**Text type:** Kotlin source code  
**Character count:** 4,115 characters  
**Rule-of-thumb:** ~4 characters per token  
**Estimated token count:** 4,115 / 4 = **1,028.75 ≈ 1,029 tokens**

I recorded this estimate before measuring the actual token count.

**Prediction:** I expect the four-characters-per-token rule to underestimate the token count for Kotlin source code because code contains punctuation, symbols, identifiers, and formatting that can be split into more tokens than ordinary English prose.

**Measured token count:** 847 tokens  
**Tokenizer used:** OpenAI Tokenizer — GPT-5.x & O1/O3 tokenizer

**Difference:** 1,029 - 847 = 182 tokens

**Estimation error:** (182 / 847) × 100 = **21.5% overestimate**

The four-characters-per-token rule overestimated the measured token count by approximately 21.5%. The actual ratio for this sample was approximately 4.86 characters per token (4,115 / 847).

### Comparison

I initially expected the four-characters-per-token rule to fit Kotlin source code worse because source code contains punctuation, symbols, identifiers, and formatting that may require additional tokens.

However, the measured results did not support that prediction. The rule overestimated the English technical prose by 27.1% and the Kotlin source code by 21.5%, so it actually fit the Kotlin sample better.

The Kotlin sample still used more tokens for roughly the same number of characters (847 tokens for 4,115 characters versus 798 tokens for 4,055 characters). This demonstrates that tokenization depends on the specific content and tokenizer, and that four characters per token should be treated as a planning estimate rather than a reliable conversion.

> **Additional observation:** I also measured the same Kotlin source code using
> the GPT-3 legacy tokenizer. The result increased from 847 tokens to 2,019
> tokens for the exact same 4,115 characters. With that tokenizer, the
> four-characters-per-token estimate of 1,029 tokens would underestimate the
> measured count by approximately 49.0%.
>
> This demonstrates that token count depends not only on the type of text but
> also on the tokenizer used by the model. A token count measured for one model
> should not be assumed to apply to another.

### Part C — Cost, honestly

For this calculation I chose **Claude Sonnet 5**, a model I could realistically use for this type of task.

**Current input price:** $2.00 per 1 million input tokens  
**Measured size of the English article:** 798 tokens
**Pricing source:** Anthropic official pricing page  
**Price checked:** September 2026

#### Cost for one request

798 / 1,000,000 × $2.00 = **$0.001596**

So, sending this page-long text once costs approximately **$0.0016 in input tokens**.

#### Cost at realistic volume

500 requests/day × 30 days = **15,000 requests/month**

798 tokens × 15,000 requests = **11,970,000 input tokens**

11.97 × $2.00 = **$23.94/month**

The estimated monthly input cost is therefore **$23.94**.

#### Does this change which model I would choose?

At this volume, the input cost alone would not make me choose a different model. I would prioritize output quality and reliability first, but I would include output-token cost and compare cheaper models before scaling to much higher traffic.

## Exercise 3 — Choose a model with my own evaluation

### Part A — Write the test set

#### Case 1 — Ordinary: Transaction loading

**Ticket**

Load a list of transactions when the screen opens.

Requirements:
- Show a loading state while transactions are being fetched.
- Display the transaction list when the request succeeds.
- Show an error state if the request fails.

**Implementation**

```kotlin
class TransactionsViewModel(
    private val repository: TransactionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUiState>(
        TransactionsUiState.Loading
    )
    val uiState: StateFlow<TransactionsUiState> = _uiState

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            val transactions = repository.getTransactions()
            _uiState.value = TransactionsUiState.Success(transactions)
        }
    }
}
```

**Question**

Review this Android/Kotlin implementation against the ticket. Identify any requirement that is not correctly handled and suggest a concrete improvement. Do not invent requirements that are not present in the ticket.

**Success criterion**

A good response must:

Identify that repository failures are not handled.
Explain that the UI would not transition to the required error state if getTransactions() fails.
Propose a concrete way to handle the failure.
Avoid inventing requirements not present in the ticket.

#### Case 2 — Ordinary: Empty state in Compose

**Ticket**

Display a list of saved payment methods.

Requirements:
- Show the saved payment methods when the list is not empty.
- Show an empty-state message when the user has no saved payment methods.
- Show a button that allows the user to add a new payment method.

**Implementation**

```kotlin
@Composable
fun PaymentMethodsScreen(
    paymentMethods: List<PaymentMethod>,
    onAddPaymentMethod: () -> Unit
) {
    Column {
        LazyColumn {
            items(paymentMethods) { paymentMethod ->
                Text(text = paymentMethod.name)
            }
        }

        Button(
            onClick = onAddPaymentMethod
        ) {
            Text("Add payment method")
        }
    }
}
```

**Question**

Review this Jetpack Compose implementation against the ticket. Identify any requirement that is not correctly handled and suggest a concrete improvement. Do not invent requirements that are not present in the ticket.

**Success criterion**

A good response must:

Identify that the empty state is not explicitly handled.
Explain that when paymentMethods is empty, the required empty-state message is never shown.
Suggest a concrete conditional UI implementation for the empty list.
Preserve the existing add-payment-method action.
Avoid inventing requirements not present in the ticket.

#### Case 3 — Edge case: Almost-empty implementation

**Ticket**

Allow the user to retry loading transactions after an error.

Requirements:
- Show a retry button when the screen is in an error state.
- Tapping the retry button must attempt to load transactions again.
- Hide the retry button when the screen is not in an error state.

**Implementation**

```kotlin
@Composable
fun TransactionsScreen(
    uiState: TransactionsUiState
) {
    // TODO
}
```

**Question**

Review this Jetpack Compose implementation against the ticket. Identify what can and cannot be concluded from the code provided. Suggest only improvements that are supported by the ticket and the implementation. Do not invent missing architecture or requirements.

**Success criterion**

A good response must:

Recognize that the provided implementation does not implement the ticket requirements.
Identify that there is no retry button or retry interaction.
Mention that the code does not provide enough information to verify how retry would trigger a new transaction load.
Avoid inventing repository, ViewModel, navigation, architecture, or other requirements not present in the input.
Suggest a minimal improvement consistent with the ticket.

#### Case 4 — Ambiguous: Transaction ordering

**Ticket**

Display the user's recent transactions.

Requirements:
- Show the transaction amount.
- Show the transaction description.
- Allow the user to open a transaction to see its details.

**Implementation**

```kotlin
@Composable
fun TransactionsScreen(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn {
        items(transactions) { transaction ->
            TransactionRow(
                amount = transaction.amount,
                description = transaction.description,
                onClick = {
                    onTransactionClick(transaction)
                }
            )
        }
    }
}
```

**Question**

Review this Jetpack Compose implementation against the ticket. Identify any requirement that is not correctly handled. If the available information is insufficient to determine whether something is correct, explicitly say so instead of assuming additional requirements.

**Success criterion**

A good response must:

Recognize that the explicitly stated requirements are implemented.
Avoid claiming that the transactions must be sorted by date, newest first, or in any other specific order.
Recognize that the word "recent" may imply some filtering or ordering behavior, but that the ticket does not define it precisely.
State that additional product clarification would be needed before treating transaction ordering or filtering as a defect.
Avoid inventing unrelated requirements.

#### Case 5 — Should not answer: Backend retry behavior

**Ticket**

Display the user's recent transactions.

Requirements:
- Show the transaction amount.
- Show the transaction description.
- Allow the user to open a transaction to see its details.

**Implementation**

```kotlin
class TransactionsViewModel(
    private val repository: TransactionsRepository
) : ViewModel() {

    fun loadTransactions() {
        viewModelScope.launch {
            repository.getTransactions()
        }
    }
}
```

**Question**

Based only on this ticket and Android/Kotlin implementation, determine how many times the backend retries the request when getTransactions() fails and what retry delay is used.

If the answer cannot be determined from the provided information, explicitly say so and do not guess.

**Success criterion**

A good response must:

State that the retry count cannot be determined from the provided information.
State that the retry delay cannot be determined from the provided information.
Explain that the behavior may exist inside TransactionsRepository, the networking layer, or the backend, but none of that implementation is provided.
Avoid inventing a retry count, delay, library, or networking strategy.

### Part B — Model A

**Model:** ChatGPT
**Configuration:** Instant
**Date tested:** September 3, 2026

#### Case 1 — Output

The implementation correctly handles two of the three ticket requirements:

* **Loading:** handled because `_uiState` starts as `TransactionsUiState.Loading`.
* **Success:** handled because a successful repository call updates the state to `Success(transactions)`.
* **Error:** **not handled**. If `repository.getTransactions()` throws an exception, the coroutine fails and `_uiState` remains `Loading`, so the UI never receives an error state.

A concrete improvement is to catch failures and update the state accordingly:

```kotlin
private fun loadTransactions() {
    viewModelScope.launch {
        try {
            val transactions = repository.getTransactions()
            _uiState.value = TransactionsUiState.Success(transactions)
        } catch (e: Exception) {
            _uiState.value = TransactionsUiState.Error
        }
    }
}
```

Assuming the UI state contains something like:

```kotlin
sealed interface TransactionsUiState {
    data object Loading : TransactionsUiState

    data class Success(
        val transactions: List<Transaction>
    ) : TransactionsUiState

    data object Error : TransactionsUiState
}
```

So the key review finding is: **the ticket explicitly requires an error state, but the current implementation has no failure handling.**

I would not add retry, empty-state handling, pagination, caching, or refresh behavior because none of those are specified in the ticket.


#### Case 2 — Output

The implementation correctly handles **2 of the 3 requirements**:

* **Non-empty list:** handled. `LazyColumn` displays each saved payment method.
* **Add payment method button:** handled. The button invokes `onAddPaymentMethod`.
* **Empty state:** **not handled**. When `paymentMethods` is empty, the `LazyColumn` simply renders nothing. The ticket explicitly requires an empty-state message.

A concrete improvement is to conditionally show a message when the list is empty:

```kotlin
@Composable
fun PaymentMethodsScreen(
    paymentMethods: List<PaymentMethod>,
    onAddPaymentMethod: () -> Unit
) {
    Column {
        if (paymentMethods.isEmpty()) {
            Text("No saved payment methods")
        } else {
            LazyColumn {
                items(paymentMethods) { paymentMethod ->
                    Text(text = paymentMethod.name)
                }
            }
        }

        Button(
            onClick = onAddPaymentMethod
        ) {
            Text("Add payment method")
        }
    }
}
```

The key review finding is: **the empty-state requirement is missing**. No loading, error handling, navigation behavior, or other states should be requested because they are not part of this ticket.


#### Case 3 — Output

The implementation does **not currently satisfy any of the ticket requirements**, because the composable contains only `// TODO`.

What **can** be concluded from the provided code:

* `TransactionsScreen` receives a `TransactionsUiState`, so it has enough information in principle to decide whether the screen is in an error state.
* There is currently no retry button.
* There is currently no retry action exposed to the composable, so tapping a button cannot trigger another load from this implementation alone.
* There is currently no conditional logic ensuring that the retry button appears only during the error state.

What **cannot** be concluded is how transactions are loaded, whether a `ViewModel` exists, what method performs the retry, or what the rest of the screen should display. The ticket does not provide enough information to prescribe any of those architectural details.

A minimal improvement supported by the ticket would be to give the composable a retry callback and show the button only for the error state:

```kotlin
@Composable
fun TransactionsScreen(
    uiState: TransactionsUiState,
    onRetry: () -> Unit
) {
    if (uiState is TransactionsUiState.Error) {
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
```

This directly addresses the three stated requirements: the button is shown in the error state, tapping it invokes a retry action, and it is absent in non-error states.

The important review distinction here is that we can say **the retry UI and retry callback are missing**, but we should **not** claim that a `ViewModel`, repository method, loading indicator, navigation behavior, or any particular architecture must be added, because none of those requirements are established by the ticket or the provided code.


#### Case 4 — Output

The implementation appears to satisfy the ticket based on the code provided.

* **Show the transaction amount:** Satisfied. `transaction.amount` is passed to `TransactionRow`.
* **Show the transaction description:** Satisfied. `transaction.description` is passed to `TransactionRow`.
* **Allow the user to open a transaction to see its details:** The screen exposes an interaction through `onTransactionClick(transaction)` when the row is clicked.

However, there are two things that **cannot be fully concluded** from this snippet:

1. We cannot verify that `TransactionRow` actually displays the `amount` and `description`, because its implementation is not provided.
2. We cannot verify that `onTransactionClick` actually opens a transaction-details screen. This composable correctly delegates the click with the selected `Transaction`, but navigation or detail presentation happens outside the code shown.

Therefore, **no ticket requirement can be identified as incorrectly implemented from the available information**. Adding assumptions such as an empty state, loading indicator, formatting, sorting, navigation architecture, or accessibility behavior would introduce requirements that are not present in the ticket.


#### Case 5 — Output

It **cannot be determined from the provided information** how many times the backend retries the request or what retry delay is used.

The implementation only shows:

```kotlin
viewModelScope.launch {
    repository.getTransactions()
}
```

There is **no retry logic visible** in the `ViewModel`, and the implementation of `TransactionsRepository.getTransactions()` is not provided.

Therefore:

* **Number of retries:** Cannot be determined.
* **Retry delay:** Cannot be determined.

It would also be incorrect to conclude that there are **zero retries**, because `getTransactions()` itself could implement retries internally. The ticket does not specify retry behavior either.

**Conclusion:** There is insufficient information to determine either the retry count or retry delay without seeing the repository implementation or additional requirements.

### Part B — Model B

**Model:** Claude
**Configuration:** Sonnet 5 Medium
**Date tested:** September 3, 2026

#### Case 1 — Output

The information needed to answer this isn't in what you've shared.

The ticket only describes UI requirements (show amount, description, allow opening details), and the implementation is a `ViewModel` that calls `repository.getTransactions()` — it doesn't show the `TransactionsRepository` implementation, any networking/HTTP client configuration (e.g., OkHttp interceptors, Retrofit), or any retry logic at all.

Retry count and retry delay are backend/network-layer concerns that would live in:
- The `TransactionsRepository` implementation
- An HTTP client's retry/interceptor configuration
- Or possibly server-side retry logic, which wouldn't be visible in Android code at all

None of that is present here, so I can't determine the retry count or delay — and I won't guess at values that aren't shown.

#### Case 2 - Output

The implementation is missing the empty-state requirement entirely.

**What's wrong:**
- The ticket asks for a message shown when `paymentMethods` is empty.
- In the current code, when the list is empty, `LazyColumn` just renders nothing — the user sees a blank space above the "Add payment method" button, with no explanation.
- The other two requirements (showing the list when non-empty, and the add button) are handled correctly.

**Concrete fix:**

```kotlin
@Composable
fun PaymentMethodsScreen(
    paymentMethods: List<PaymentMethod>,
    onAddPaymentMethod: () -> Unit
) {
    Column {
        if (paymentMethods.isEmpty()) {
            Text(
                text = "You have no saved payment methods",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(paymentMethods) { paymentMethod ->
                    Text(text = paymentMethod.name)
                }
            }
        }

        Button(
            onClick = onAddPaymentMethod
        ) {
            Text("Add payment method")
        }
    }
}
```

Two things worth calling out:
1. Branching on `paymentMethods.isEmpty()` is the key change — it directly satisfies the "show empty-state message" requirement.
2. I added `Modifier.weight(1f)` to the `LazyColumn` so it doesn't try to take infinite height inside the `Column` if the `Column`'s height is unbounded (e.g. wrapped in a scrollable parent) — this isn't a new requirement, just a correctness detail so the layout doesn't crash or behave oddly depending on the parent container. If `PaymentMethodsScreen` is always used with a fixed-height parent (e.g. inside a `Scaffold`), this may not be strictly necessary, but it's safe either way.


#### Case 3 — Output

The code block you've shared doesn't actually contain an implementation — the body of `TransactionsScreen` is just a `// TODO` comment. There's no logic to evaluate against the ticket's requirements.

**What can be concluded:**
- The function signature takes a `uiState: TransactionsUiState` parameter, so the screen does receive some state object from outside. Nothing else is knowable — the fields/shape of `TransactionsUiState` aren't shown, so I can't tell whether it currently models an "error state" at all.

**What cannot be concluded:**
- Whether a retry button exists, is conditionally shown, or is wired to any tap handler — none of this is implemented yet.
- Whether `uiState` has an error case, a way to distinguish error vs. non-error, or a retry/reload function — the type isn't defined here, only referenced.
- Anything about how "loading transactions again" would be triggered (callback, lambda parameter, ViewModel call, etc.) — no such mechanism appears in the signature.

**Improvements — strictly scoped to what's given:**
Since there's no implementation, the only supportable suggestion is: the function body needs to be written to satisfy the three ticket requirements (conditional retry button visibility based on error state, a tap action that re-triggers loading, and the button hidden otherwise). I can't recommend *how* to structure that (e.g., what `UiState` should look like, whether to use a sealed class, whether retry is a lambda param or something else) because none of that is specified in the code or ticket — inventing it would go beyond what's supported by the material provided.

If you can share the actual `TransactionsUiState` definition and the intended function body, I can give a concrete review against the three requirements.

#### Case 4 — Output

A few things stand out:

**Amount and description display — insufficient information.** `TransactionsScreen` passes `transaction.amount` and `transaction.description` into `TransactionRow`, but `TransactionRow`'s implementation isn't shown here. Whether these values are actually rendered (and rendered legibly — correct formatting, not empty/hidden) depends on that composable's body, which isn't included. I can't confirm this requirement is met without seeing it.

**Opening a transaction to see details — correctly wired, as far as this snippet goes.** The `onClick` lambda calls `onTransactionClick(transaction)` with the specific item from the loop, so the right transaction is passed up. Whether that actually results in a details view being shown (navigation, dialog, etc.) happens outside this composable, so that part is also not verifiable from what's given — it depends on how the caller implements `onTransactionClick`.

**One implementation issue not tied to a stated requirement:** `items(transactions)` doesn't supply a `key`. Without one, `LazyColumn` uses list position as the default item identity, so if `transactions` is ever reordered, filtered, or has items inserted/removed (e.g., a new transaction arrives at the top, or the list refreshes), Compose can misattribute recomposition state across rows and skip/mis-animate transitions. This isn't something the ticket calls for explicitly, so I'm flagging it as a robustness concern rather than an unmet requirement — worth confirming whether `Transaction` has a stable ID (e.g., `transaction.id`) to use as the key.

Nothing else in the ticket's three requirements appears violated by what's shown, but the two "insufficient information" points above are the ones I'd want the missing code (or a stated expectation) for before signing off.

#### Case 5 — Output

The ticket and implementation don't specify anything about retry behavior for `getTransactions()`.

Looking at what's given:
- The ticket only covers UI requirements (show amount, description, allow opening details).
- The ViewModel just calls `repository.getTransactions()` inside a coroutine launch — there's no retry logic, no `retry`/`retryWhen` operator, no delay/backoff configuration, and the `TransactionsRepository` implementation isn't shown at all.

Since retry count and delay would be implemented inside `TransactionsRepository.getTransactions()` (or in an underlying network layer like OkHttp/Retrofit interceptors), and none of that code is provided, this cannot be determined from the given information. I won't guess a number of retries or a delay value.

If you want a real answer, you'd need to share the `TransactionsRepository` implementation (and any network client/interceptor configuration it relies on).

## Part C — Evaluation Criteria

Each model response is evaluated using the same five criteria. Each criterion is scored from **1 to 5**, for a maximum of **25 points per case**.

### 1. Correctness

Evaluates whether the technical conclusions are accurate based on the ticket and the provided implementation.

- **5 — Excellent:** All conclusions are technically correct.
- **4 — Good:** Mostly correct, with a minor technical issue that does not affect the main conclusion.
- **3 — Acceptable:** The main conclusion is correct, but there are noticeable technical inaccuracies.
- **2 — Poor:** Contains significant technical errors.
- **1 — Incorrect:** The main conclusion is incorrect.

### 2. Groundedness

Evaluates whether the response stays within the information actually provided by the ticket and implementation.

- **5 — Excellent:** Clearly distinguishes what is known from what cannot be determined.
- **4 — Good:** Mostly grounded, with minor speculation that does not affect the conclusion.
- **3 — Acceptable:** Makes some unsupported assumptions.
- **2 — Poor:** Relies substantially on information that was not provided.
- **1 — Ungrounded:** Invents or assumes critical information needed for the answer.

### 3. Requirement Adherence

Evaluates whether the response follows the explicit instructions in the question.

This includes instructions such as:

- identifying unmet requirements;
- explicitly stating when information is insufficient;
- not inventing additional requirements;
- suggesting only improvements supported by the ticket and implementation.

Scoring:

- **5 — Excellent:** Follows all explicit instructions.
- **4 — Good:** Follows the instructions with one minor deviation.
- **3 — Acceptable:** Follows the main instruction but ignores or weakly follows a secondary constraint.
- **2 — Poor:** Violates an important instruction.
- **1 — Failed:** Does not follow the core task.

### 4. Unsupported Assumptions

Evaluates whether the model introduces requirements, architecture, implementation details, bugs, or recommendations that are not supported by the provided material.

A higher score means fewer unsupported assumptions.

- **5 — None:** Introduces no unsupported assumptions or unnecessary requirements.
- **4 — Minor:** Includes a small amount of unnecessary speculation or an extra recommendation, clearly separated from the ticket requirements.
- **3 — Moderate:** Introduces one or more unsupported assumptions that meaningfully expand the scope.
- **2 — Significant:** Relies on unsupported assumptions when evaluating the implementation.
- **1 — Severe:** Hallucinates requirements or implementation details and treats them as facts.

### 5. Conciseness and Relevance

Evaluates whether the response answers the question directly without unnecessary tangents.

- **5 — Excellent:** Direct, focused, and contains only information useful to the requested review.
- **4 — Good:** Slightly verbose or includes a minor tangent.
- **3 — Acceptable:** Contains noticeable unnecessary detail but still answers the question clearly.
- **2 — Poor:** Significant portions are irrelevant or distract from the requested task.
- **1 — Very Poor:** The response is largely unfocused or fails to provide a clear answer.

### Scoring

Each case has a maximum score of **25 points**:

| Criterion | Maximum |
|---|---:|
| Correctness | 5 |
| Groundedness | 5 |
| Requirement Adherence | 5 |
| Unsupported Assumptions | 5 |
| Conciseness and Relevance | 5 |
| **Total per case** | **25** |

With five test cases, each model can receive a maximum overall score of **125 points**.

The same rubric must be applied to every model and every case. Scores should be based only on the ticket, implementation, question, and model output provided in this experiment.

### Case 1 — Evaluation

| Criterion | ChatGPT Instant | Claude Sonnet 5 Medium |
|---|---:|---:|
| Correctness | 5/5 | 5/5 |
| Groundedness | 5/5 | 5/5 |
| Requirement Adherence | 5/5 | 5/5 |
| Unsupported Assumptions | 5/5 | 5/5 |
| Conciseness and Relevance | 5/5 | 4/5 |
| **Total** | **25/25** | **24/25** |

#### ChatGPT Instant

ChatGPT correctly concluded that neither the retry count nor the retry delay can be determined from the provided information.

A particularly strong part of the response was explicitly recognizing that the absence of retry logic in the `ViewModel` does **not** prove that there are zero retries. Retry behavior could exist inside `TransactionsRepository.getTransactions()`.

The response stayed within the available evidence, directly answered both parts of the question, and did not invent retry values or additional requirements.

**Result: 25/25**

#### Claude Sonnet 5 Medium

Claude reached the same correct conclusion and clearly explained that the repository implementation and networking configuration were not provided.

It also correctly refused to infer a retry count or delay from the visible `ViewModel` code.

The response received one fewer point for conciseness because it expanded into several possible locations where retry behavior might exist, including repository, HTTP client configuration, and server-side behavior. These possibilities are reasonable, but they were not necessary to answer the question.

Importantly, these examples were presented as possibilities rather than facts, so they do not count as unsupported assumptions.

**Result: 24/25**

#### Case 1 Conclusion

Both models handled the uncertainty correctly and avoided the most important failure mode in this case: interpreting the absence of visible retry logic as evidence of zero retries.

ChatGPT Instant was slightly more concise, while there was no meaningful difference in correctness or groundedness.

### Case 2 — Evaluation

| Criterion | ChatGPT Instant | Claude Sonnet 5 Medium |
|---|---:|---:|
| Correctness | 5/5 | 4/5 |
| Groundedness | 5/5 | 4/5 |
| Requirement Adherence | 5/5 | 4/5 |
| Unsupported Assumptions | 5/5 | 4/5 |
| Conciseness and Relevance | 5/5 | 4/5 |
| **Total** | **25/25** | **20/25** |

#### ChatGPT Instant

ChatGPT correctly identified the missing requirement: the implementation does not display an empty-state message when `paymentMethods` is empty.

It also correctly recognized that the other two ticket requirements are handled by the provided implementation:

- payment methods are rendered when present;
- the "Add payment method" button is always available and invokes `onAddPaymentMethod`.

The suggested improvement directly addressed the missing requirement without introducing unrelated architecture, behavior, or additional requirements.

**Result: 25/25**

#### Claude Sonnet 5 Medium

Claude correctly identified the same missing empty-state requirement and proposed the appropriate core fix: branching on `paymentMethods.isEmpty()` and displaying an empty-state message.

However, the response also introduced `Modifier.weight(1f)` and a discussion about preventing the `LazyColumn` from taking infinite height depending on its parent container.

This recommendation is not supported by the ticket or by the implementation context provided. The parent layout is unknown, and the question explicitly asked not to invent requirements that are not present in the ticket.

Claude did clearly separate this recommendation from the ticket requirement by describing it as a "correctness detail," which limits the severity of the issue. The core ticket analysis remains correct.

The extra discussion also makes the response less concise than necessary.

**Result: 20/25**

#### Case 2 Conclusion

Both models correctly identified the actual ticket violation: the missing empty state.

ChatGPT Instant remained strictly scoped to the requirements and provided only the necessary improvement. Claude Sonnet 5 Medium solved the required issue correctly but expanded the review with an additional layout recommendation that was not justified by the provided ticket or implementation context.

This case therefore shows a meaningful difference in **scope discipline**: both models reached the correct answer, but ChatGPT adhered more strictly to the instruction not to introduce unsupported requirements or improvements.

### Case 3 — Evaluation

| Criterion | ChatGPT Instant | Claude Sonnet 5 Medium |
|---|---:|---:|
| Correctness | 5/5 | 5/5 |
| Groundedness | 5/5 | 5/5 |
| Requirement Adherence | 5/5 | 5/5 |
| Unsupported Assumptions | 5/5 | 5/5 |
| Conciseness and Relevance | 5/5 | 4/5 |
| **Total** | **25/25** | **24/25** |

#### ChatGPT Instant

ChatGPT correctly recognized that the `TransactionsScreen` body contains only a `// TODO`, so none of the three ticket requirements can be confirmed as implemented.

The response appropriately avoided inventing missing architecture or assuming how retry behavior should be structured.

It stayed focused on what can actually be concluded from the provided code and suggested only that the missing implementation must handle the ticket requirements.

**Result: 25/25**

#### Claude Sonnet 5 Medium

Claude also correctly recognized that there is no implementation to review and carefully separated what can and cannot be concluded.

A particular strength was identifying that even though the function receives a `TransactionsUiState`, its definition is not provided. Therefore, the model correctly avoided assuming that the type contains an error state or any particular state representation.

Claude also explicitly avoided recommending a sealed class, callback structure, ViewModel interaction, or other architecture that was not supported by the ticket or implementation.

The response was slightly more verbose than necessary, particularly in enumerating multiple examples of architecture that it intentionally would not assume. However, those examples were clearly presented as things that **cannot** be inferred, rather than as recommendations.

**Result: 24/25**

#### Case 3 Conclusion

Both models handled the incomplete implementation correctly and respected the instruction not to invent missing architecture.

Claude Sonnet 5 Medium was especially explicit about the limits of what could be inferred from `TransactionsUiState`, while ChatGPT Instant reached the same essential conclusion more concisely.

There is no meaningful difference in correctness, groundedness, or requirement adherence in this case; the only scoring difference is conciseness.

### Case 4 — Evaluation

| Criterion | ChatGPT Instant | Claude Sonnet 5 Medium |
|---|---:|---:|
| Correctness | 5/5 | 4/5 |
| Groundedness | 5/5 | 5/5 |
| Requirement Adherence | 5/5 | 4/5 |
| Unsupported Assumptions | 5/5 | 4/5 |
| Conciseness and Relevance | 5/5 | 3/5 |
| **Total** | **25/25** | **20/25** |

#### ChatGPT Instant

ChatGPT concluded that the implementation satisfies the three requirements based on the code provided:

- `transaction.amount` is passed to `TransactionRow`;
- `transaction.description` is passed to `TransactionRow`;
- clicking a row invokes `onTransactionClick(transaction)`.

This is a reasonable review at the abstraction boundary shown by the implementation. The screen correctly supplies the required data and interaction to `TransactionRow`.

The response did not invent additional UI, navigation, formatting, performance, or architecture requirements.

**Result: 25/25**

#### Claude Sonnet 5 Medium

Claude took a more conservative interpretation of the available evidence.

For the amount and description requirements, it argued that because the implementation of `TransactionRow` is not provided, it cannot verify that those values are ultimately rendered. For the interaction requirement, it correctly recognized that `onTransactionClick(transaction)` is wired, while noting that the actual navigation or details presentation occurs outside the provided composable.

This reasoning is strongly grounded in the available evidence. However, it is arguably stricter than necessary for reviewing `TransactionsScreen`: the visible implementation passes both required values and the click interaction to the row abstraction, which is sufficient evidence that this screen handles those responsibilities.

More importantly, Claude introduced an additional recommendation concerning the absence of a stable `key` in `items(transactions)`. Stable keys can be useful in `LazyColumn`, but the ticket does not mention list mutations, state preservation, animations, ordering, or stable identity. The response itself acknowledges that this issue is not tied to a stated requirement.

Because the question specifically asks to identify requirements that are not correctly handled and to avoid assuming additional requirements, this robustness discussion expands the review beyond the requested scope.

**Result: 20/25**

#### Case 4 Conclusion

The main difference in this case is the interpretation of the implementation boundary.

ChatGPT Instant treats passing `amount`, `description`, and `onClick` into `TransactionRow` as sufficient evidence that `TransactionsScreen` handles the ticket requirements.

Claude Sonnet 5 Medium applies a stricter evidentiary standard and avoids claiming that behavior inside `TransactionRow` or the external navigation callback is guaranteed without seeing those implementations. This is a defensible grounding strategy, but it makes the review more conservative than necessary at the abstraction level provided.

The clearer weakness in Claude's response is the additional `LazyColumn` key recommendation, which is outside the stated ticket requirements and reduces both scope adherence and conciseness.

### Case 5 — Evaluation

| Criterion | ChatGPT Instant | Claude Sonnet 5 Medium |
|---|---:|---:|
| Correctness | 5/5 | 5/5 |
| Groundedness | 5/5 | 5/5 |
| Requirement Adherence | 5/5 | 5/5 |
| Unsupported Assumptions | 5/5 | 5/5 |
| Conciseness and Relevance | 5/5 | 4/5 |
| **Total** | **25/25** | **24/25** |

#### ChatGPT Instant

ChatGPT correctly concluded that neither the retry count nor the retry delay can be determined from the provided information.

The response correctly observed that the visible implementation only launches a coroutine and calls `repository.getTransactions()`. Because the repository implementation is not provided, the absence of retry logic in the `ViewModel` is not sufficient evidence to conclude that no retries occur.

It explicitly avoided the incorrect inference that the retry count must be zero and directly answered both parts of the question.

**Result: 25/25**

#### Claude Sonnet 5 Medium

Claude reached the same correct conclusion and explicitly stated that neither the ticket nor the visible implementation specifies retry behavior.

It correctly noted that no retry operators, delays, or backoff configuration are visible and that the implementation of `TransactionsRepository.getTransactions()` is unavailable.

The references to possible networking-layer implementations such as OkHttp or Retrofit were presented only as examples of where retry behavior could exist, not as claims that those technologies are actually being used. Therefore, they do not constitute unsupported assumptions.

The response is slightly more verbose than necessary because it discusses several possible implementation locations and finishes by requesting additional repository and networking code. These details are reasonable but not required to answer the question.

**Result: 24/25**

#### Case 5 Conclusion

Both models handled the uncertainty correctly and avoided inventing retry behavior.

There is no meaningful difference in correctness, groundedness, requirement adherence, or unsupported assumptions. ChatGPT Instant receives a slightly higher score only because it provides the same conclusion more concisely.

This case reinforces the result from Case 1: both models are capable of recognizing when retry behavior cannot be inferred from the visible Android/Kotlin code rather than assuming that missing retry logic means zero retries.

## Part D — Final Comparison

### Overall Scores

| Model | Score | Percentage |
|---|---:|---:|
| ChatGPT Instant | **125/125** | **100%** |
| Claude Sonnet 5 Medium | **112/125** | **89.6%** |

### Final Observations

Both models demonstrated strong technical understanding of Android/Kotlin code and were generally effective at distinguishing between information that was explicitly available and information that could not be determined.

The main difference between the models was not technical knowledge, but **scope discipline**.

#### ChatGPT Instant

ChatGPT consistently stayed close to the ticket, implementation, and explicit instructions.

Across the five cases, it:

- identified missing requirements without introducing unrelated improvements;
- distinguished visible behavior from behavior hidden behind abstractions;
- explicitly stated when information was insufficient;
- avoided interpreting missing code as evidence that a behavior does not exist;
- avoided introducing additional architecture, performance, UI, or implementation requirements;
- provided concise answers focused on the requested review.

This was particularly visible in the retry cases, where ChatGPT correctly refused to conclude that there were zero retries simply because retry logic was not visible in the `ViewModel`.

It also performed well when reviewing incomplete implementations, avoiding assumptions about missing `UiState`, repository, or networking implementations.

#### Claude Sonnet 5 Medium

Claude also demonstrated strong technical reasoning and was particularly careful about uncertainty.

Across several cases, it explicitly distinguished between what could be observed directly and what would require additional implementation details to verify.

This was especially strong in Case 3, where Claude avoided making assumptions about the structure of `TransactionsUiState`, and in Cases 1 and 5, where it correctly refused to infer retry behavior.

However, Claude showed a recurring tendency to expand beyond the exact scope of the ticket.

Examples included:

- recommending `Modifier.weight(1f)` in Case 2;
- discussing parent layout constraints that were not provided;
- introducing stable `LazyColumn` keys in Case 4;
- discussing additional networking implementation possibilities when a shorter uncertainty statement would have been sufficient.

These observations were generally technically reasonable and were often explicitly identified by Claude as being outside the stated requirements. Therefore, they should not be treated as hallucinations.

However, in a constrained code-review task where the instruction explicitly says not to invent additional requirements or architecture, these additions reduce requirement adherence and relevance.

### Key Finding

The experiment suggests an important distinction between **technical correctness** and **grounded technical review**.

A response can contain technically useful advice while still being less effective for the task if that advice is not supported by the ticket or implementation being reviewed.

ChatGPT Instant was more consistent at answering:

> What can I conclude from exactly what I have been given?

Claude Sonnet 5 Medium more frequently expanded toward:

> What else might be technically relevant to this implementation?

Both approaches can be valuable in real engineering work, but the first approach performed better under the constraints of this experiment.

### Conclusion

Both ChatGPT Instant and Claude Sonnet 5 Medium produced technically strong responses and successfully avoided major hallucinations across the five Android/Kotlin review cases.

ChatGPT Instant achieved the higher score because it consistently maintained a tighter boundary around the provided evidence and explicit requirements.

Claude Sonnet 5 Medium demonstrated excellent awareness of uncertainty, but occasionally introduced additional implementation concerns that were technically plausible yet unsupported by the ticket.

For tasks where the objective is strict requirement validation, grounded code review, or determining whether available evidence is sufficient to support a conclusion, **ChatGPT Instant performed better in this experiment**.

The most significant lesson from the comparison is that strong model performance in code review is not only about identifying technically valid improvements. It is also about knowing **when not to infer, when not to expand the scope, and when the correct answer is simply that the available information is insufficient**.