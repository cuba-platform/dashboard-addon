# Audimex-Dashboards

## Abstract
Dashboard designer allows you to build a dashboard with your frames and predefined layouts such as vertical, horizontal and grid in WYSIWYG mode.

## Key concepts
Designer contains a widget palette, widget tree with hierarchical structure and canvas where you can build and see your dashboard. Also, it's possible to build dashboard using Drag-n-Drop from Palette to Canvas / Tree.

Dashboard public methods:
 - `setDashboardModel` sets a dashboard model to frame.
 - `getDashboardModel` returns a dashboard model of frame.

## Structure
Main components of this application component:
 - `DashboardFrame` contains main controls and canvas.
 - `DashboardModel` contains component hierarchical structure. Can be serializaed to any format (JSON/XML).
 - `WidgetRepository` loads widgets configuration from XML config files (widget caption, icon, frameId) that are set to `<...>` config property.

## How to add to a project
1. Open the project in Studio
2. Go to ProjectProperties > Edit
3. Add new custom component
4. Enter `com.audimex.dashboard` to the artifact group field
5. Enter `dashboard-global` to the artifact name field
6. Enter `0.1-SNAPSHOT` to the version field
7. Click `Ok`

## How to integrate to a screen
1. Open an XML of your screen
2. Add new frame to layout `<frame id="myFrame" screen="dashboard-frame"/>` with a screen attribute and set it to `dashboard-frame`
3. Inject the dashboard frame into your screen controller.
4. Now you can use `setDashboardModel` and `getDashboardModel` to save and load a data model of the dashboard.

## How to register a widget
At first, create an XML file in the web module and specify your frames
```
<widgets xmlns="http://schemas.haulmont.com/audimex/dashboards/widget-descriptor.xsd">
    <widget id="first" caption="firstFrame" frameId="first-frame" icon="icons/ok.png"/>
    <widget id="second" caption="secondFrame" frameId="second-frame" icon="icons/ok.png"/>
</widgets>
```
XML config of the widget repository is defined in widget-descriptor.xsd

Then specify your XML path (or several space-separated paths) in the `amxd.dashboard.widgetsConfig` config:
```
amxd.dashboard.widgetsConfig = widgets.xml
```
Now you can see your widgets in the dashboard palette.

## Extensibility: how to customize behavior
Also, you can override the behavior of the `DashboardSettings` and `WidgetRepository`.
In the `DashboardSettingsImpl` you can define any components to restrict their dragging.
In the `WidgetRepositoryImpl` you can redefine a widget loading to load it from any source