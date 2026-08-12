# Fix: Hardcoded UI Disconnect

Move business state and logic from Composables to ViewModels in `ExpenseEditScreen` and `ProteinEditScreen`.

## Proposed Changes

### [Component Name] Expenses

#### [MODIFY] [ExpenseViewModel.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/expenses/ui/ExpenseViewModel.kt)
- Add `onKeyPress(key: String)` and `onBackspace()` to handle keypad logic.
- Add `categories` to `ExpenseEditUiState` or as a constant.

#### [MODIFY] [ExpenseEditScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/expenses/ui/ExpenseEditScreen.kt)
- Remove hardcoded tabs (Income/Expense/Transfer) as they are not in the data model.
- Connect `NumericKeypad` to `viewModel.onKeyPress` and `viewModel.onBackspace`.
- Use `categories` from ViewModel.

### [Component Name] Protein

#### [MODIFY] [ProteinViewModel.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/protein/ui/ProteinViewModel.kt)
- Add `onKeyPress(key: String)` and `onBackspace()` to handle keypad logic.

#### [MODIFY] [ProteinEditScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/protein/ui/ProteinEditScreen.kt)
- Remove hardcoded tabs (Food/Supplement) as they are not in the data model.
- Connect `NumericKeypad` to `viewModel.onKeyPress` and `viewModel.onBackspace`.

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.

### Manual Verification
- Verify that the numeric keypad correctly updates the amount in both Expense and Protein edit screens.
- Verify that categories in the Expense screen are populated correctly.
- Verify that the "Save" button still works as expected.
