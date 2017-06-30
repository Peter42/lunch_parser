# Access
The productive version is avaiable at https://temp.philipp1994.de/sap/lunch/

To access the API use https://temp.philipp1994.de/sap/lunch/api/v1/

# API
The API Levels (currently there is only one) will keep their documented scheme (without giving any warranty here, but it's planned) with the exception of possibly added fields in the future, e.g.:
* Additional GET parameters
* Additional fields (keys) in the JSON object

## v1
The Webservice returns a JSON which looks similar to this one:
```json
{
    "menus": [
        {
            "name": "MRI",
            "lunchItems": [
                {
                    "itemName": "Gegrillter Hirtenkäse mit Balkangarnitur und Bulgur",
                    "price" : -1
                }, {
                    "itemName": "Ochsenbrust mit Grüner Frankfurter Sauce und Bratkartoffel",
                    "price" : -1
                }
            ]
        }, {
            "name": "Pizzahaus",
            "lunchItems": [
                {
                    "itemName": "Pasta con Polpette, insalata verde",
                    "price" : 5.5
                }, {
                    "itemName": "Maiale alla Griglia, Pommes, insalata verde",
                    "price" : 8
                }
            ]
        }
    ],
    "generationTime": 1496920099,
    "menuForDay": 1496880000
}
```
timestamps (`generationTime` and `menuForDay`) use unixtime format.

price is in EUR, -1 => price unkown.

# Icon
Download Icon here: https://materialdesignicons.com/icon/food

Use Android Asset Studio to edit: https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html#foreground.type=image&foreground.space.trim=1&foreground.space.pad=0.25&foreColor=rgba(96%2C%20125%2C%20139%2C%200)&backColor=rgb(204%2C%20204%2C%20204)&crop=0&backgroundShape=hrect&effects=none&name=ic_launcher
