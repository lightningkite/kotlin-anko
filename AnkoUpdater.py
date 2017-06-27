import os
import re

rootdir = os.path.dirname(os.path.abspath(__file__))

kt_replacements = [
    (re.compile(r'button\((\w)'), r'button(text = \1'),
    (re.compile(r'textView\((\w)'), r'textView(text = \1'),
    (re.compile(r'checkBox\((\w)'), r'checkBox(text = \1'),
    (re.compile(r'editText\((\w)'), r'editText(text = \1'),
    (re.compile(r'radioButton\((\w)'), r'radioButton(text = \1'),
    (re.compile(r'imageButton\((\w)'), r'imageButton(imageResource = \1'),
    (re.compile(r'imageView\((\w)'), r'imageView(imageResource = \1')
]
kt_plain_replacements = [
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