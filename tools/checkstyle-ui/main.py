import os

from textual.app import App, ComposeResult
from textual.widgets import Tree

from xml.etree import ElementTree


class CheckstyleUI(App):

    def __init__(self, path):
        super().__init__()
        self.checkstyle_root = ElementTree.parse(path).getroot()

    def compose(self) -> ComposeResult:
        tree = Tree("Files")

        for f in self.checkstyle_root.findall('.//file'):
            name = os.path.basename(f.get('name'))
            l = tree.root.add(name, expand=False)
            ctr = 0
            for e in f.findall('./error'):
                msg = e.get('message')
                msg += ' -- ' + e.get('source') + ''
                l.add_leaf(msg)
                ctr += 1
            l.label += ' [' + str(ctr) + ']'
            
        tree.root.expand()
        yield tree


if __name__ == '__main__':
    app = CheckstyleUI('../../backend/notes_service/target/checkstyle-result.xml')
    app.run()

