#!/bin/bash

# This script migrates Java files from com.windsurf to com.springcloud package

# Define source and target directories
SRC_DIR="src/main/java/com/windsurf/agentportal"
TARGET_DIR="src/main/java/com/springcloud/agentportal"

# Create target directories if they don't exist
mkdir -p "$TARGET_DIR"

# Find all Java files in source directory and process them
find "$SRC_DIR" -type f -name "*.java" | while read file; do
    # Get relative path to base dir
    rel_path="${file#$SRC_DIR/}"
    target_file="$TARGET_DIR/$rel_path"
    
    # Create target directory if it doesn't exist
    target_dir=$(dirname "$target_file")
    mkdir -p "$target_dir"
    
    # Copy file with package name replacement
    sed 's/package com\.windsurf\.agentportal/package com.springcloud.agentportal/g' "$file" > "$target_file"
    
    # Replace any import statements referencing windsurf
    sed -i '' 's/import com\.windsurf\.agentportal/import com.springcloud.agentportal/g' "$target_file"
    
    echo "Migrated: $file -> $target_file"
done

echo "Migration completed. Please review files and update any remaining references."
