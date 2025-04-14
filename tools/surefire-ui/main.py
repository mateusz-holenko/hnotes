import os

from textual.app import App, ComposeResult
from textual.widgets import DataTable, TabbedContent, TabPane, Label, Static
from textual.binding import Binding

from xml.etree import ElementTree


class TestsTable(DataTable):
    BINDINGS = [
        Binding('j', 'cursor_down', 'Move cursor down'),
        Binding('k', 'cursor_up', 'Move cursor up'),
        Binding('h', 'cursor_left', 'Move cursor left'),
        Binding('l', 'cursor_right', 'Move cursor right')
    ]

    def __init__(self):
        super().__init__()

    def on_data_table_row_highlighted(self, x):
        t = self.app.query_one(TabbedContent)
        t.clear_panes()
        c = self.app.cases[x.cursor_row]
        fp = None
        for y in c:
            p = TabPane(y.tag, Static(y.text, markup=False))
            fp = fp if fp else p
            t.add_pane(p)
        if fp:
            t.active = fp.id


class SurefireUI(App):
    def __init__(self, path):
        super().__init__()
        self.reports_directory = os.path.join(path, 'target', 'surefire-reports')
        self.cases = []

    def compose(self) -> ComposeResult:
        yield TestsTable()
        yield TabbedContent()

    def on_mount(self) -> None:
        table = self.query_one(DataTable)
        table.add_columns('result', 'class', 'name', 'time')
        table.cursor_type = 'row'

        for root, dirs, files in os.walk(self.reports_directory):
            for file in files:
                if not file.endswith('xml'):
                    continue
                xml_root = ElementTree.parse(os.path.join(root, file)).getroot()
                cases = xml_root.findall('.//testcase')
                for case in cases:
                    has_error = len(case.findall('error')) > 0
                    name = case.get('name')
                    classname = case.get('classname')
                    time = case.get('time')
                    self.cases.append(case)
                    table.add_row('[red]-[/]' if has_error else '[green]ok[/]', classname, name, time)
            


if __name__ == '__main__':
    app = SurefireUI('../../backend/notes_service')
    app.run()

