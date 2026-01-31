# Git Commands Reference - MT104 Feature Implementation

This document contains all the Git commands used during the MT104 feature implementation for educational purposes.

## 🌿 Branch Management

### Creating and Switching to Feature Branch
```bash
# Check current branch status
git status

# Create and switch to a new feature branch
git checkout -b feature/mt104

# Alternative: Create branch and switch separately
git branch feature/mt104
git checkout feature/mt104
```

### Viewing Branch Information
```bash
# List all local branches
git branch

# List all branches (local and remote)
git branch -a

# Show current branch
git branch --show-current
```

## 📝 Staging and Committing Changes

### Staging Files
```bash
# Stage specific files
git add src/main/java/com/qoomon/banking/swift/ui/controller/ParserController.java
git add src/main/java/com/qoomon/banking/swift/ui/controller/ComposerController.java
git add src/main/resources/templates/composer.html
git add src/main/resources/templates/documentation.html

# Stage all files in a directory
git add src/main/java/com/qoomon/banking/swift/ui/controller/
git add src/main/resources/templates/

# Stage all changes (use with caution)
git add .

# Stage specific file and commit in one go
git add handover.md && git commit -m "docs: update handover with MT104 details"
```

### Committing Changes
```bash
# Commit with a descriptive message
git commit -m "feat: implement MT104 support (restored and cleaned)"
git commit -m "docs: add MT104 example to Examples section"
git commit -m "docs: update handover with MT104 details"

# Commit with multi-line message (opens editor)
git commit
```

### Commit Message Conventions
```bash
# Feature additions
git commit -m "feat: implement MT104 support"

# Documentation updates
git commit -m "docs: add MT104 example to documentation"

# Bug fixes
git commit -m "fix: resolve parser issue with MT104 headers"

# Refactoring
git commit -m "refactor: extract MT104 logic to separate method"

# Build/deployment changes
git commit -m "build: update Dockerfile for MT104 support"
```

## 🔄 Synchronizing with Remote

### Pushing Changes
```bash
# Push to remote feature branch
git push origin feature/mt104

# Force push (use carefully - overwrites remote history)
git push origin feature/mt104 --force

# Push and set upstream tracking
git push -u origin feature/mt104
```

### Pulling Changes
```bash
# Pull latest changes from remote
git pull origin main

# Pull with rebase instead of merge
git pull --rebase origin main
```

## 🔍 Viewing History and Changes

### Viewing Commit History
```bash
# View recent commits (one line per commit)
git log --oneline -5

# View detailed commit history
git log

# View commits with file changes
git log --stat

# View commits for a specific file
git log -- src/main/resources/templates/documentation.html
```

### Viewing Differences
```bash
# View unstaged changes
git diff

# View staged changes
git diff --cached

# Compare branches
git diff main..feature/mt104

# View changes in a specific file
git diff src/main/resources/templates/composer.html
```

### Checking Status
```bash
# View current working tree status
git status

# Short status format
git status -s
```

## 🔀 Merging Branches

### Merging Feature Branch to Main
```bash
# Switch to main branch
git checkout main

# Pull latest changes from remote main
git pull origin main

# Merge feature branch into main
git merge feature/mt104

# Push merged changes to remote
git push origin main

# All in one command (used in this project)
git checkout main && git pull origin main && git merge feature/mt104 && git push origin main
```

### Merge Types
```bash
# Fast-forward merge (default when possible)
git merge feature/mt104

# Force merge commit even if fast-forward is possible
git merge --no-ff feature/mt104

# Squash all commits into one
git merge --squash feature/mt104
```

## ⏮️ Undoing Changes

### Undoing Commits
```bash
# Undo last commit but keep changes staged
git reset --soft HEAD~1

# Undo last commit and unstage changes
git reset HEAD~1

# Undo last commit and discard all changes (DANGEROUS)
git reset --hard HEAD~1

# Undo multiple commits
git reset --hard HEAD~3
```

### Discarding Changes
```bash
# Discard changes in a specific file
git checkout -- filename.txt

# Discard all unstaged changes
git checkout -- .

# Remove untracked files
git clean -fd
```

## 🏷️ Tagging

### Creating Tags
```bash
# Create lightweight tag
git tag v1.0.0

# Create annotated tag with message
git tag -a v1.0.0 -m "Release version 1.0.0 with MT104 support"

# Push tags to remote
git push origin v1.0.0

# Push all tags
git push origin --tags
```

## 🔧 Advanced Commands

### Stashing Changes
```bash
# Stash current changes
git stash

# Stash with a message
git stash save "WIP: MT104 implementation"

# List stashes
git stash list

# Apply most recent stash
git stash apply

# Apply and remove stash
git stash pop

# Drop a stash
git stash drop
```

### Cherry-picking
```bash
# Apply a specific commit to current branch
git cherry-pick <commit-hash>

# Cherry-pick without committing
git cherry-pick -n <commit-hash>
```

### Rebasing
```bash
# Rebase current branch onto main
git rebase main

# Interactive rebase (edit last 3 commits)
git rebase -i HEAD~3

# Continue after resolving conflicts
git rebase --continue

# Abort rebase
git rebase --abort
```

## 📊 Workflow Summary - MT104 Implementation

Here's the complete workflow used for the MT104 feature:

```bash
# 1. Create feature branch
git checkout -b feature/mt104

# 2. Make changes to files
# (Edit ParserController.java, ComposerController.java, etc.)

# 3. Stage and commit changes
git add src/main/java/com/qoomon/banking/swift/ui/controller/
git add src/main/resources/templates/
git commit -m "feat: implement MT104 support (restored and cleaned)"

# 4. Push to remote
git push origin feature/mt104

# 5. Continue development
git add handover.md
git commit -m "docs: update handover with MT104 details"
git push origin feature/mt104

# 6. Add final touches
git add src/main/resources/templates/documentation.html
git commit -m "docs: add MT104 example to Examples section"
git push origin feature/mt104

# 7. Merge to main
git checkout main
git pull origin main
git merge feature/mt104
git push origin main

# 8. Verify merge
git log --oneline -5
```

## 🎓 Best Practices

### Commit Messages
- Use present tense ("add feature" not "added feature")
- Use imperative mood ("move cursor to..." not "moves cursor to...")
- Keep first line under 50 characters
- Use conventional commit prefixes: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`

### Branching Strategy
- Use descriptive branch names: `feature/mt104`, `bugfix/parser-error`, `hotfix/security-patch`
- Keep feature branches short-lived
- Regularly sync with main branch
- Delete merged branches

### General Tips
- Commit early and often
- Write meaningful commit messages
- Review changes before committing (`git diff`)
- Pull before pushing to avoid conflicts
- Use `.gitignore` to exclude unnecessary files
- Never commit sensitive information (passwords, API keys)

---
*Generated on 2026-01-30 for the MT104 Feature Implementation*
