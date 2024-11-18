package com.digitalfrontiers.olddataconnectiontool.validation

//TODO idk if the is a better name for this
class YahooAutosDatafeedValidator : OutputValidator() {

    override val schema: String = """
        {
          "type": "object",
          "title": "Yahoo! Autos Used Car Data Feed",
          "properties": {
              "seq_no": {
                "type": "integer",
                "description": "Format: This is your unique number, provided by the client. This field is like DB Primary Key of this record. It cannot exceed 20 digits. It must be a positive integer, starting from 1, 2, 3...99999999999999999999. You cannot write: 001"
              },
              "brand": {
                "type": "string",
                "maxLength": 25,
                "description": "Car manufacturer name, limited to 25 characters, wrapped in CDATA. Please refer to the  below Car manufacturer names API information to obtain the name of the car manufacturer. Please use make_en_name in this field. For example: Lexus"
              },
              "model": {
                "type": "string",
                "maxLength": 25,
                "description": "Format: text, limited to 25 words, and wrapped in CDATA. Please refer to the above Car manufacturer names API information to obtain the value of the 'make_en_name' to fill in the below Car series names API."
              },
              "year": {
                "type": "string",
                "pattern": "^\\d{4}${'$'}",
                "description": "Year of manufacture in YYYY format."
              },
              "month": {
                "type": "string",
                "description": "Factory month in MM format. Optional field."
              },
              "displacement": {
                "type": "integer",
                "minimum": 1,
                "description": "Displacement, positive integer."
              },
              "price": {
                "type": "integer",
                "minimum": 1,
                "description": "Price, positive integer."
              },
              "area": {
                "type": "string",
                "maxLength": 3,
                "description": "Format: Refer to the 'County and City Region' table in the spreadsheet link below. Select the county where the car is located, limited to 3 Chinese characters, and enclosed in CDATA."
              },
              "area_show": {
                "type": "string",
                "maxLength": 50,
                "description": "Format: free text, limited to 50 Chinese characters, and wrapped in CDATA. Optional field"
              },
              "desc": {
                "type": "string",
                "maxLength": 50,
                "description": "Car description, limited to 50 Chinese characters, wrapped in CDATA. Optional field."
              },
              "img_url": {
                "type": "string",
                "format": "uri",
                "description": "Format: jpg/gif, 400x225, limited to 40kb. Please put the original location of the image in the URL of the image, for example: https://www.hotcar.com.tw/UPLOAD/CW/HE3006/1381520.jpg"
              },
              "link_url": {
                "type": "string",
                "format": "uri",
                "description": "Car link URL."
              },
              "gear": {
                "type": "string",
                "enum": ["AT", "MT", "AMT"],
                "description": "Gear type. AT: Automatic, MT: Manual, AMT: Semi-Automatic or Automated Manual."
              },
              "color": {
                "type": "string",
                "maxLength": 25,
                "description": "Car color, limited to 25 Chinese characters, wrapped in CDATA. Optional field."
              },
              "update_time": {
                "type": "string",
                "pattern": "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}${'$'}",
                "description": "Last updated time, in the format yyyy-mm-dd hh:mm:ss."
              },
              "certificate": {
                "type": "string",
                "enum": ["Y", "N"],
                "description": "Certified used car status. Y: Yes, N: No.  Optional field"
              },
              "show_status": {
                "type": "string",
                "enum": ["Y", "N"],
                "description": "Car listing status. Y: Listed, N: Unlisted."
              }
            },
            "required": [
              "seq_no",
              "brand",
              "model",
              "year",
              "displacement",
              "price",
              "area",
              "img_url",
              "link_url",
              "gear",
              "update_time",
              "show_status"
            ]
          },
          "required": ["carset"]
        }

    """.trimIndent()


}