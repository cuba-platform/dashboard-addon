# Audimex-Dashboards

## Abstract
Dashboard designer allows you to build a dashboard with your frames and predefined layouts such as vertical, horizontal and grid in WYSIWYG mode.

## Key concepts
Designer contains a widget palette, widget tree with hierarchical structure and canvas where you can build and see your dashboard. Also, it's possible to build dashboard using Drag-n-Drop from Palette to Canvas / Tree. The widget can be depended from an entity type value. The designer show a two types of widget. The first type of widget is a widget which doesn't include an entity type. The second type of widget is a widget which equal of a selected entity type of dashboard. If on the dashboard the entity type is not set, then an only widgets with not setted an entity type will be available for choising. Otherwise the widgets for equal type and without type will be avaible.

Dashboard public methods:
 - `setDashboardModel` sets a dashboard model to frame.
 - `getDashboardModel` returns a dashboard model of frame.
 - `setDashboardMode` sets a type of the dashboard view. In VIEW mode it can display only non-editable canvas and DESIGNER mode is default mode with widget palette and component structure tree.
 - `getDashboardMode` returns a dashboard display mode type.
     
Dashboard parameters:

```
@WindowParam(name = "DASHBOARD_ENTITY")
protected Entity entity;
```

This parameter describe the entity which will using for an algorithm calculation.

Also the dashboard provides different options for input parameters.

User can input three types of values:

```
ENTITY(10)
LIST_ENTITY(20)
DATE(30)
INTEGER(40)
STRING(50)
DECIMAL(60)
BOOLEAN(70)
LONG(80)
UNDEFINED(90)
```

As well the user can define a type of the parameter input type. The system supports following input types:

```
OUTER(10)
ALGORITHM(20)
INPUT(30)
```

The type "OUTER" means that the parameter will fill from outside. This parameter should be filled in a parameter map, which is incoming in the "init()" method.
The type "INPUT" means that a parameter should be filled during configuration of the dashboard.

The system calculates parameters during execution, before a screen drawing. The values of parameters will be stored into a widget link entity. 

## Structure
Main components of this application component:
 - `DashboardFrame` contains main controls and canvas.
 - `DashboardModel` contains component hierarchical structure. Can be serializaed to any format (JSON/XML).
 - `WidgetRepository` loads widgets configuration from XML config files (widget caption, icon, frameId) that are set to `<widget .../>` config property.
 - `Dashboard` entity has `user`, `title` and `model` properties. `model` contains JSON structure as a String. Also, this entity is bounded to `DashboardEdit` and `DashboardBrowse` screens and can be used in your project.
 - `WidgetLinkModel` contains data for relationships between dashboard widget and widget parameters.
 - `WidgetParameterModel` contains widget parameters values.

## How to add to a project
1. Open the project in Studio
2. Go to ProjectProperties > Edit
3. Add new custom component
4. Enter `com.audimex.dashboard` to the artifact group field
5. Enter `dashboard-global` to the artifact name field
6. Enter `0.1-SNAPSHOT` to the version field
7. Click `Ok`

This component supports PostgreSQL and HSQLDB databases.

## How to integrate to a screen
1. Open an XML of your screen
2. Add new frame to layout `<frame id="myFrame" screen="dashboard-frame"/>` with a screen attribute and set it to `dashboard-frame`
3. Inject the dashboard frame into your screen controller.
4. Now you can use `setDashboardModel` and `getDashboardModel` to save and load a data model of the dashboard.

## How to register a widget
At first, create an XML file in the web module and specify your frames
```
<widgets xmlns="http://schemas.haulmont.com/audimex/dashboards/widget-descriptor.xsd">
    <widget id="first" caption="firstFrame" frameId="first-frame" icon="icons/boat.png"/>
    <widget id="second" caption="secondFrame" frameId="second-frame" icon="font-icon:CAR"/>
</widgets>
```
XML config of the widget repository is defined in widget-descriptor.xsd
If you want to use your own `.png` icons, you can put it inside of theme extension like `themes/halo/icons/boat.png` and then specify it in `icon` attribute like `icon="icons/boat.png"`
Also, using of FontAwesome icons is possible too. Just specify it in icon attribute with `font-icon` prefix and icon ID like `icon="font-icon:CAR"`

Then specify the relative path (or several space-separated paths) of your XML file in the `amxd.dashboard.widgetsConfig` config:
```
amxd.dashboard.widgetsConfig = widgets.xml
```
Now you can see your widgets in the dashboard palette.

## Extensibility: how to customize behavior
Also, you can override the behavior of the `DashboardSettings` and `WidgetRepository`.
In the `DashboardSettingsImpl` you can define any components to restrict their dragging.
In the `WidgetRepositoryImpl` you can redefine a widget loading to load it from any source