# Contributing

## Thank You

Thank you for your interest in contributing to Omega Chess! This guide will help you get started.

## Links

- [Repository](https://github.com/dmccoystephenson/Omega-Chess)

## Requirements

- A GitHub account
- Git installed on your local machine
- A Java IDE or text editor (IntelliJ IDEA recommended)
- A basic understanding of Java

## Getting Started

1. [Sign up for GitHub](https://github.com/signup) if you don't have an account.
2. Fork the repository by clicking **Fork** at the top right of the repo page.
3. Clone your fork: `git clone https://github.com/<your-username>/Omega-Chess.git`
4. Open the project in your IDE.
5. Build the project: `./gradlew build`
   If you encounter errors, please open an issue.

## Identifying What to Work On

### Issues

Work items are tracked as [GitHub issues](https://github.com/dmccoystephenson/Omega-Chess/issues).

### Milestones

Issues are grouped into [milestones](https://github.com/dmccoystephenson/Omega-Chess/milestones) representing upcoming releases.

## Making Changes

1. Make sure an issue exists for the work. If not, create one.
2. Switch to `develop`: `git checkout develop`
3. Create a branch: `git checkout -b <branch-name>`
4. Make your changes.
5. Test your changes.
6. Commit: `git commit -m "Description of changes"`
7. Push: `git push origin <branch-name>`
8. Open a pull request against `develop`, link the related issue with `#<number>`.
9. Address review feedback.

## Testing

Run the unit tests with:

Linux: `./gradlew clean test`
Windows: `.\gradlew.bat clean test`

For manual testing:

1. Start the server by running `OCMultiServer.main()`.
2. Run the desktop client by running `DesktopLauncher.main()` with `true` as a program argument to use the local server.

## Questions

Open a GitHub Discussion or issue in this repository.
