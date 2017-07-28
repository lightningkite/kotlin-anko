import os
import re

rootdir = os.path.dirname(os.path.abspath(__file__))

kt_replacements = [
    #(re.compile(r'\.selector\(([^\)]+)\) *{ *\n'), r'.selector(\1){ dialog, it -> ')
]
kt_plain_replacements = [
	("@onClick", "@setOnClickListener")
]

def do_replace_kt(text):
    for pattern, replace in kt_plain_replacements:
        text = text.replace(pattern, replace)
    for pattern, replace in kt_replacements:
        text = pattern.sub(replace, text)
    return text


def do_replace_on_file(path):
    if path.endswith('.kt'):
        text = open(path).read()
        afterText = do_replace_kt(text)
        open(path, 'w').write(afterText)

for subdir, dirs, files in os.walk(rootdir):
    for file in files:
        path = os.path.join(subdir, file)
        if 'build' in path:
            continue
        print path
        do_replace_on_file(path)
