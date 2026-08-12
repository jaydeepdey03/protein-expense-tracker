# Implementation Plan - Add Delete Option for Expense and Protein

Add the ability for users to delete individual expense and protein entries from both the history lists and the edit screens.

## Proposed Changes

### [Dashboard Component](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/dashboard)

#### [MODIFY] [DashboardScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/dashboard/DashboardScreen.kt)
- Update `ListItemCard` to accept an optional `onDelete` lambda.
- Add a delete icon (IconButton) to the `Row` in `ListItemCard` if `onDelete` is provided.

### [Expenses Feature](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/expenses)

#### [MODIFY] [ExpenseListScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/expenses/ui/ExpenseListScreen.kt)
- Pass `viewModel::deleteExpense` to `ListItemCard`.

#### [MODIFY] [ExpenseEditScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/expenses/ui/ExpenseEditScreen.kt)
- Add a "Delete" button (OutlinedButton with error color) next to the "Save" button or as an icon in the TopAppBar when `expenseId` is not null.

### [Protein Feature](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/protein)

#### [MODIFY] [ProteinListScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/protein/ui/ProteinListScreen.kt)
- Pass `viewModel::deleteProtein` to `ListItemCard`.

#### [MODIFY] [ProteinEditScreen.kt](file:///Users/jaydeepdey/AndroidStudioProjects/TrackingApp/app/src/main/java/com/jaydeep/trackingapp/features/protein/ui/ProteinEditScreen.kt)
- Add a "Delete" button (OutlinedButton with error color) when `proteinId` is not null.

## Verification Plan

### Automated Tests
- Run Gradle build to ensure no regressions.
- (Optional) I can check if there are existing tests for repositories to add a delete test case, but the logic is already there in the repository.

### Manual Verification
- Deploy the app to a device.
- Navigate to "Expense History" or "Protein History".
- Verify that a delete icon appears and works.
- Navigate to the "Edit" screen for an entry.
- Verify that a "Delete" button appears and works.
