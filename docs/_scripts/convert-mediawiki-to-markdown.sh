#!/bin/bash

# This script converts mediawiki formatted files in the current
# working directory to markdown using pandoc.

for FILE in *.mediawiki; do
    echo "Processing $FILE"
    FILENAME=$(basename $FILE .mediawiki)
    sed ':a; N; $!ba; s/```yaml\([^`]*\)```/<syntaxhighlight lang="yaml">\1<\/syntaxhighlight>/g; s/```\([^`]*\)```/<syntaxhighlight>\1<\/syntaxhighlight>/g' $FILE | pandoc -f mediawiki -t gfm -o "$FILENAME.md"
done
