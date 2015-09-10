
import sys
import re

start = re.compile("^ *// *(\w*) *\[ *$")
end = re.compile("^ *// *\] *$")

snippets = []
snippet = None
for line in sys.stdin:
    if snippet:
        snippet["content"] = snippet["content"] + line

    match = start.match(line)
    if match:
        snippet = {"name": match.group(1), "content": ""}

    match = end.match(line)
    if match:
        snippets.append(snippet)
        snippet = None

for s in snippets:
    name = s["name"]
    print "writing snippet " + name
    f = open("snippets/" + name, 'w')
    f.write(s["content"])


