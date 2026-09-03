
@Composable
fun SlidingWindowTimelineScreen(
    modifier: Modifier = Modifier,
    viewModel: SlidingWindowLabViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SlidingWindowTimelineContent(
        modifier = modifier,
        uiState = uiState,
        onPreviousClick = viewModel::previousStep,
        onNextClick = viewModel::nextStep,
        onRestartClick = viewModel::restart
    )
}

@Composable
fun SlidingWindowTimelineContent(
    modifier: Modifier = Modifier,
    uiState: SlidingWindowUiState,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRestartClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SlidingWindowHeader(
            experimentTitle = uiState.experimentTitle,
            currentStep = uiState.currentStepIndex + 1,
            totalSteps = uiState.totalSteps
        )

        val currentStep = uiState.currentStep
        if (currentStep == null) {
            Text("No steps available")
            return@Column
        }

        SlidingWindowVisualization(
            text = currentStep.text,
            left = currentStep.left,
            right = currentStep.right
        )

        CurrentWindowCard(
            currentWindow = currentStep.currentWindow
        )

        Spacer(modifier = Modifier.height(12.dp))

        FrequencyMapCard(
            frequencies = currentStep.frequencies
        )

        NavigationButtons(
            canGoPrevious = uiState.canGoPrevious,
            canGoNext = uiState.canGoNext,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
            onRestartClick = onRestartClick
        )
    }
}

@Composable
fun NavigationButtons(
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onPreviousClick,
            enabled = canGoPrevious,
            modifier = Modifier.weight(1f)
        ) {
            Text("Previous")
        }

        OutlinedButton(
            onClick = onNextClick,
            enabled = canGoNext,
            modifier = Modifier.weight(1f)
        ) {
            Text("Next")
        }

        OutlinedButton(
            onClick = onRestartClick,
            modifier = Modifier.weight(1f)
        ) {
            Text("Restart")
        }
    }
}

@Composable
fun SlidingWindowVisualization(
    text: String,
    left: Int,
    right: Int,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(text.toList()) { index, character ->
            val isInsideWindow =
                index in left..right

            val pointer = getPointerLabel(
                index,
                left,
                right
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                CharacterIndex(
                    index = index,
                    isInsideWindow = isInsideWindow
                )
                CharacterBox(
                    isInsideWindow = isInsideWindow,
                    character = character,
                    size = 60.dp
                )

                Pointer(pointer)
            }
        }
    }
}

@Composable
fun CharacterIndex(
    index: Int,
    isInsideWindow: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = index.toString(),
        style = MaterialTheme.typography.labelSmall,
        color = if (isInsideWindow) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        modifier = modifier
    )
}