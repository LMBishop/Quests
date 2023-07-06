if [[ -z $1 || -z $2 ]]; then
    echo "Usage: $0 <old tag> <latest tag>"
    exit 1
fi

if [ ! -f "UPDATE_MESSAGE" ]; then
    echo "Error: no UPDATE_MESSAGE file in current directory"
    exit 1
fi

echo "Generating changelog between $1 and $2"

# BBCode for Spigot
cat UPDATE_MESSAGE > CHANGELOG_BBCODE
# Markdown for everything else but GitHub
cat UPDATE_MESSAGE > CHANGELOG_MD
# GitHub flavoured markdown for... GitHub
cat UPDATE_MESSAGE > CHANGELOG_GFM

{ echo; echo "[SIZE=6][B]Commit log since $1[/B][/SIZE]"; echo; } >> CHANGELOG_BBCODE
{ echo; echo "## Commit log since $1"; echo; } >> CHANGELOG_MD
{ echo; echo "## Commit log since $1"; echo; } >> CHANGELOG_GFM

git log "$1".."$2" --reverse --pretty="format:[URL='https://github.com/LMBishop/Quests/commit/%H']%h[/URL] - %s <%an>" >> CHANGELOG_BBCODE
git log "$1".."$2" --reverse --pretty="format:[\`%h\`](https://github.com/LMBishop/Quests/commit/%H) - %s \\<%an\\>  " >> CHANGELOG_MD
git log "$1".."$2" --reverse --pretty="format:%h - %s \\<@%an\\>  " >> CHANGELOG_GFM

{ echo; echo; echo "[SIZE=6][B]More information[/B][/SIZE]"; echo; } >> CHANGELOG_BBCODE
{ echo; echo; echo "## More information"; echo; } >> CHANGELOG_MD
{ echo; echo; echo "## More information"; echo; } >> CHANGELOG_GFM

FOOTER=$(echo "* Documentation: https://quests.leonardobishop.com"; echo "* Report a bug: https://github.com/LMBishop/Quests/issues"; echo "* Contribute to Quests: https://github.com/LMBishop/Quests/pulls";)
FOOTER_BBCODE=$(echo "[LIST]"; echo "[*]Documentation: https://quests.leonardobishop.com"; echo "[*]Report a bug: https://github.com/LMBishop/Quests/issues"; echo "[*]Contribute to Quests: https://github.com/LMBishop/Quests/pulls"; echo "[/LIST]";)
echo "$FOOTER_BBCODE" >> CHANGELOG_BBCODE
echo "$FOOTER" >> CHANGELOG_MD
echo "$FOOTER" >> CHANGELOG_GFM
