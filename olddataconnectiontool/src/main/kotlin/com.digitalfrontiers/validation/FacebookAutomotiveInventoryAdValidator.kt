package com.digitalfrontiers.validation

//TODO idk if the is a better name for this
class FacebookAutomotiveInventoryAdValidator : OutputValidator() {

    // this schema was manually created based on the description at https://developers.facebook.com/docs/marketing-api/auto-ads/reference#vehicle
    override val schema : String = """
        {
          "title": "Automotive Inventory Ads - Supported Fields - Vehicle",
          "description": "See https://developers.facebook.com/docs/marketing-api/auto-ads/reference#vehicle",
          "type": "object",
          "properties": {
            "fb_page_id": {
              "type": "integer",
              "description": "Required for inventory ads with on-Facebook destination. Facebook page ID of the partner or dealership. Your catalog must be added to the allow list by your Facebook representative before you can send your page IDs. The page must be added to the allow list using the Business Manager before can be ingested. See Managing Permissions."
            },
            "vehicle_id": {
              "type": "string",
              "maxLength": 100,
              "description": "Required. Max characters: 100. Unique ID for item. Can be a variant for a vehicle. If there are multiple instances of the same ID, we ignore all instances. You can also use the VIN for both vehicle_id and vin. Example: 1FADP5AU6DL536022"
            },
            "title": {
              "type": "string",
              "maxLength": 500,
              "description": "Required. Max characters: 500. Full name of vehicle. This is relevant and specific to each vehicle and it should contain what is set in year, make, model, trim. Example: ${'$'}299 per month for the EndoHatch GE"
            },
            "description": {
              "type": "string",
              "maxLength": 5000,
              "description": "Required. Max characters: 5000. Short text describing the vehicle. Don't include promotional text or any links. Don't enter text in all capital letters. Use line breaks to format your description. Example: Used 2017 Volvo XC90 in great condition, available now."
            },
            "url": {
              "type": "string",
              "format": "uri",
              "description": "Required. Link to the external site where you can view the vehicle listing."
            },
            "make": {
              "type": "string",
              "description": "Required. Make or brand of the vehicle. Example: Endomoto"
            },
            "model": {
              "type": "string",
              "description": "Required. Model of the vehicle. Example: EndoHatch"
            },
            "year": {
              "type": "integer",
              "description": "Required. Year the vehicle was launched in yyyy format. Example: 2015"
            },
            "mileage": {
              "type": "object",
              "properties": {
                "value": {
                  "type": "integer",
                  "description": "Required. For used vehicles, current mileage of the vehicle in kilometers (kms) or miles (MI). For new vehicles, use zero (0). For Marketplace, vehicles must have over 500 miles/kms. Example: 1500"
                },
                "unit": {
                  "type": "string",
                  "enum": ["MI", "KM"],
                  "description": "Required. Mileage units: MI (miles) or KM (kilometers)"
                }
              }
            },
            "images": {
              "type": "array",
              "maxItems": 20,
              "items": {
                "type": "object",
                "properties": {
                  "url": {
                    "type": "string",
                    "description": "Required. Max items: 20. URL of the vehicle image. If you have more than one vehicle image, follow this naming convention: image[1].url, image[2].url, and so on. You must provide at least one image. Each image can be up to 4 MB in size. For Marketplace, a 2-image minimum is required. To use carousel ads — Provide a square 1:1 aspect ratio images (600x600px). To show single vehicle ads — Provide images with 1.91:1 aspect ratio image (1200x630px)."
                  },
                  "tags": {
                    "type": "array",
                    "items": {
                      "type": "string"
                    },
                    "description": "Optional. Max items: 20. Tag appended to the image that shows what's in the image. There can be multiple tags associated with an image. Example: Exterior, Interior, StockImage"
                  }
                }
              }
            },
            "transmission": {
              "type": "string",
              "enum": ["Automatic", "Manual"],
              "description": "Optional. Transmission type of the vehicle: Automatic or Manual."
            },
            "body_style": {
              "type": "string",
              "enum": ["CONVERTIBLE", "COUPE", "HATCHBACK", "MINIVAN", "TRUCK", "SUV", "SEDAN", "VAN", "WAGON", "CROSSOVER", "SMALL_CAR", "OTHER"],
              "description": "Required. Body style of the vehicle: CONVERTIBLE, COUPE, HATCHBACK, MINIVAN, TRUCK, SUV, SEDAN, VAN, WAGON, CROSSOVER, SMALL_CAR, or OTHER."
            },
            "drivetrain": {
              "type": "string",
              "enum": ["4X2", "4X4", "AWD", "FWD", "RWD", "Other"],
              "description": "Optional. Vehicle drivetrain. Supported values: 4X2, 4X4, AWD, FWD, RWD, Other."
            },
            "vin": {
              "type": "string",
              "maxLength": 17,
              "description": "Optional. Max characters: 17. Vehicle ID number (VIN) of the vehicle. You can also use the VIN for both vehicle_id and vin. Note: The VIN must be exactly 17 characters and it isn't required for pre-1983 vehicles. Boats have less digits and some vehicles (such as trailers) don't need VINs. Example: 1FADP5AU6DL536022"
            },
            "price": {
              "type": "string",
              "description": "Required. Cost and currency of the vehicle. Format the price as the cost, followed by the ISO currency code, with a space between cost and currency. Example: 18000 USD, 32000 USD"
            },
            "exterior_color": {
              "type": "string",
              "description": "Required. Vehicle color. Example: Black, White, Blue, Red"
            },
            "state_of_vehicle": {
              "type": "string",
              "enum": ["New", "Used", "CPO"],
              "description": "Required. Current state of the vehicle. Supported values: New, Used, or CPO (certified pre-owned)."
            },
            "fuel_type": {
              "type": "string",
              "enum": ["DIESEL", "ELECTRIC", "FLEX", "GASOLINE", "HYBRID", "OTHER"],
              "description": "Optional. Vehicle fuel type. Supported values: DIESEL, ELECTRIC, FLEX, GASOLINE, HYBRID, OTHER."
            },
            "tag": {
              "type": "string",
              "description": "Optional. String that describes the image. There can be multiple tags associated with an image. Example: Exterior, Interior, StockImage"
            },
            "chrome_id": {
              "type": "integer",
              "description": "Optional. Similar to autodata_id."
            },
            "condition": {
              "type": "string",
              "enum": ["EXCELLENT", "GOOD", "FAIR", "POOR", "OTHER"],
              "description": "Optional. Condition of the vehicle. Supported values: EXCELLENT, GOOD, FAIR, POOR, OTHER."
            },
            "sale_price": {
              "type": "integer",
              "description": "Optional. Sale price or special price. Format the price as the cost, followed by the ISO currency code, with a space between cost and currency. Example: 16000 USD"
            },
            "availability": {
              "type": "string",
              "enum": ["available", "not available"],
              "description": "Optional. Vehicle availability: available or not available. We don't show vehicles that are unavailable in the ad."
            },
            "vehicle_type": {
              "type": "string",
              "enum": ["car_truck", "boat", "commercial", "motorcycle", "powersport", "rv_camper", "trailer", "other"],
              "description": "Optional. Type of vehicle. Expected values: car_truck (default if not supplied), boat, commercial, motorcycle, powersport, rv_camper, trailer, or other."
            },
            "trim": {
              "type": "string",
              "maxLength": 50,
              "description": "Optional. Max characters: 50. Trim of the vehicle. Example: 5DR HB SE"
            },
            "interior_color": {
              "type": "string",
              "maxLength": 50,
              "description": "Optional. Max characters: 50. Vehicle interior color."
            },
            "date_first_on_lot": {
              "type": "string",
              "format": "date",
              "description": "Optional. Date when this vehicle first arrived at the dealer lot. Used to indicate inventory age. Use the yyyy-mm-dd format. Example: 2018-09-05"
            },
            "days_on_lot": {
              "type": "integer",
              "description": "Optional. Number of days the vehicle has been on the lot. Should be incremented daily. Example: 62"
            },
            "status": {
              "type": "string",
              "enum": ["active", "archived"],
              "description": "Optional. Controls whether an item is active or archived in your catalog. Only active items can be seen by people in your ads, shops or any other channels. Supported values: active, archived. Items are active by default. Learn more about archiving items. Example: active. Note: Some partner platforms such as Shopify may sync items to your catalog with a status called staging, which behaves the same as archived. This field was previously called visibility. While we still support the old field name, we recommend that you use the new name."
            },
            "custom_number_0": {
              "type": "integer",
              "minimum": 0,
              "maximum": 4294967295,
              "description": "Up to five custom fields for any additional number-related information you want to filter items by when you create sets. This field allows you to filter by number ranges (is greater than and is less than) when you create a set. For example, you could use this field to indicate the year an item was produced, and then filter a certain year range into a set. This field supports whole numbers between 0 and 4294967295. It doesn't support negative numbers, decimal numbers or commas, such as -2, 5.5 or 10,000. Example: 2022"
            },
            "custom_number_1": {
              "type": "integer",
              "minimum": 0,
              "maximum": 4294967295,
              "description": "Up to five custom fields for any additional number-related information you want to filter items by when you create sets. This field allows you to filter by number ranges (is greater than and is less than) when you create a set. For example, you could use this field to indicate the year an item was produced, and then filter a certain year range into a set. This field supports whole numbers between 0 and 4294967295. It doesn't support negative numbers, decimal numbers or commas, such as -2, 5.5 or 10,000. Example: 2022"
            },
            "custom_number_2": {
              "type": "integer",
              "minimum": 0,
              "maximum": 4294967295,
              "description": "Up to five custom fields for any additional number-related information you want to filter items by when you create sets. This field allows you to filter by number ranges (is greater than and is less than) when you create a set. For example, you could use this field to indicate the year an item was produced, and then filter a certain year range into a set. This field supports whole numbers between 0 and 4294967295. It doesn't support negative numbers, decimal numbers or commas, such as -2, 5.5 or 10,000. Example: 2022"
            }
          },
          "required": [
            "vehicle_id",
            "title",
            "description",
            "url",
            "make",
            "model", 
            "year",
            "mileage",
            "body_style",
            "price",
            "exterior_color",
            "state_of_vehicle"
          ]
        }
    """.trimIndent()

}