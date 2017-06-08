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
        			"itemName": "Gegrillter Hirtenkäse mit Balkangarnitur und Bulgur"
        		}, {
        			"itemName": "Ochsenbrust mit Grüner Frankfurter Sauce und Bratkartoffel"
        		}
    		]
    	}, {
    		"name": "Pizzahaus",
    		"lunchItems": [
        		{
        			"itemName": "Pasta con Polpette, insalata verde"
        		}, {
        			"itemName": "Maiale alla Griglia, Pommes, insalata verde"
        		}
    		]
    	}
	],
	"generationTime": 1496920099,
	"menuForDay": 1496880000
}
```
timestamps (`generationTime` and `menuForDay`) use unixtime format.
