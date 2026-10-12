import zipfile, sys
from xml.etree import ElementTree as ET

path = r"C:/Users/MCfywb/Downloads/毕业设计/学生宿舍管理系统-系统说明书.docx"
ns = {'w': 'http://schemas.openxmlformats.org/wordprocessingml/2006/main'}
W = '{http://schemas.openxmlformats.org/wordprocessingml/2006/main}'

z = zipfile.ZipFile(path)
xml = z.read('word/document.xml')
root = ET.fromstring(xml)
body = root.find('w:body', ns)

def para_text(p):
    return ''.join(t.text or '' for t in p.iter(W+'t'))

out = []
for el in body:
    tag = el.tag.split('}')[1]
    if tag == 'p':
        pPr = el.find(W+'pPr', ns)
        style = ''
        if pPr is not None:
            pStyle = pPr.find(W+'pStyle', ns)
            if pStyle is not None:
                style = pStyle.get(W+'val')
        txt = para_text(el)
        prefix = f"[{style}] " if style else ""
        out.append(prefix + txt)
    elif tag == 'tbl':
        out.append("==== TABLE ====")
        for ri, row in enumerate(el.findall(W+'tr', ns)):
            cells = []
            for tc in row.findall(W+'tc', ns):
                ctext = " / ".join(para_text(p) for p in tc.findall(W+'p', ns))
                cells.append(ctext)
            out.append("ROW%d: " % ri + " | ".join(cells))
        out.append("==== /TABLE ====")

text = "\n".join(out)
with open(r"D:/code/Student-Dormitory-Management-System/_tmp_design_spec_text.txt", "w", encoding="utf-8") as f:
    f.write(text)
print("CHARS:", len(text))
print("LINES:", len(out))
