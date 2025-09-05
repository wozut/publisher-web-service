#!/bin/bash

echo "Setting up Git hooks..."

HOOKS_DIR=".git/hooks"
PROJECT_HOOKS_DIR="git-hooks"

# Create hooks directory if it doesn't exist
mkdir -p "$HOOKS_DIR"

# Create pre-push hook that executes the project hook
if [ -f "$PROJECT_HOOKS_DIR/pre-push" ]; then
    cat > "$HOOKS_DIR/pre-push" << 'EOF'
#!/bin/bash
exec ./git-hooks/pre-push "$@"
EOF
    chmod +x "$HOOKS_DIR/pre-push"
    chmod +x "$PROJECT_HOOKS_DIR/pre-push"
    echo "✅ Pre-push hook installed"
else
    echo "❌ Pre-push hook not found in $PROJECT_HOOKS_DIR"
fi

echo "Git hooks setup complete!"