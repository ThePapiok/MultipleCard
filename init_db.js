conn = new Mongo();
db = conn.getDB("multipleCard")

db.createCollection("shops", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "name", "totalAmount", "imageUrl", "password", "role"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "name": {
                    "bsonType": "string",
                    "description": "name is required and must be string"
                },
                "totalAmount": {
                    "bsonType": "long",
                    "minimum": 0,
                    "description": "totalAmount is required and must be greater or equal 0"
                },
                "imageUrl": {
                    "bsonType": "string",
                    "description": "imageUrl is required and must be string"
                },
                "password": {
                    "bsonType": "string",
                    "description": "password is required and must be string"
                },
                "role": {
                    "enum": ["ROLE_SHOP"],
                    "description": "role is required and must be ROlE_SHOP"
                }
            }
        }
    }
}
);
db.createCollection("products", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "name", "description", "imageUrl", "barcode", "categoryId", "amount", "shopId"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "name":  {
                    "bsonType": "string",
                    "description": "name is required and must be string"
                },
                "description": {
                    "bsonType": "string",
                    "description": "description in review is required and must be string"
                },
                "imageUrl": {
                    "bsonType": "string",
                    "description": "imageUrl is required and must be string"
                },
                "barcode": {
                    "bsonType": "string",
                    "description": "barcode is required and must be string"
                },
                "categoryId": {
                    "bsonType": "objectId",
                    "description": "categoryId is required and must be objectId" 
                },
                "amount": {
                    "bsonType": "int",
                    "minimum": 0,
                    "description": "amount is required and must be greater or equal 0"
                },
                "shopId": {
                    "bsonType": "objectId",
                    "description": "shopId is required and must be objectId" 
                }
            }
        }
    }
}
);
db.createCollection("orders", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "userId", "productId", "createdAt", "isUsed", "amount"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "userId": {
                    "bsonType": "objectId",
                    "description": "userId is required and must be objectId" 
                },
                "productId": {
                    "bsonType": "objectId",
                    "description": "productId is required and must be objectId" 
                },
                "createdAt": {
                    "bsonType": "date",
                    "description": "createdAt is required and must be date"
                },
                "isUsed": {
                    "bsonType": "bool",
                    "description": "isUsed is required and must be bool"
                }, 
                "amount": {
                    "bsonType": "int",
                    "minimum": 0,
                    "description": "amount is required and must be greater or equal 0"
                }
            }
        }
    }
}
);
db.createCollection("categories", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "name"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "name":  {
                    "bsonType": "string",
                    "description": "name is required and must be string"
                }
            }
        }
    }
}
);
db.createCollection("countries", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["name", "code", "_id"],
            "additionalProperties": false,
            "properties": {
                "name": {
                    "bsonType": "string",
                    "description": "name is required and must be string"
                },
                "code": {
                    "bsonType": "string",
                    "description": "code is required and must be string"
                },
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                }
            }
        }
    }
}
);
db.createCollection("likes", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id","reviewUserId", "userId"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "reviewUserId": {
                    "bsonType": "objectId",
                    "description": "reviewUserId is required and must be objectId"
                },
                "userId": {
                    "bsonType": "objectId",
                    "description": "userId is required and must be objectId"
                }
            }
        }
    }
});
db.createCollection("users", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "firstName", "lastName", "countryId", "phone", "city", "postalCode", "street", "houseNumber", "apartamentNumber", "password", "card" , "isActive", "points", "verificationNumber", "role", "review"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "firstName": {
                    "bsonType": "string",
                    "description": "firstName is required and must be string"
                },
                "lastName": {
                    "bsonType": "string",
                    "description": "lastName is required and must be string"
                },
                "countryId": {
                    "bsonType": "objectId",
                    "description": "countryId is required and must be objectId"
                },
                "phone": {
                    "bsonType": "string",
                    "description": "phone is required and must be string"
                },
                "city": {
                    "bsonType": "string",
                    "description": "city is required and must be string"
                },
                "postalCode": {
                    "bsonType": "string",
                    "description": "postalCode is required and must be string"
                },
                "street": {
                    "bsonType": "string",
                    "description": "street is required and must be string"
                },
                "houseNumber": {
                    "bsonType": "int",
                    "minimum": 1,
                    "description": "houseNumber is required, must be int and must be greater than 0"
                },
                "apartamentNumber": {
                    "bsonType": ["int", "null"],
                    "minimum": 1,
                    "description": "apartamentNumber should be int or null and must be greater than 0"
                },
                "password": {
                    "bsonType": "string",
                    "description": "password is required and must be string"
                },
                "isActive": {
                    "bsonType": "bool",
                    "description": "isActive is required and must be bool"
                },
                "points": {
                    "bsonType": "int",
                    "minimum": 0,
                    "description":
                        "points is required, must be int and greater or equal than 0"
                },
                "verificationNumber": {
                    "bsonType": "string",
                    "description": "string is required and must be string"
                },
                "role": {
                    "enum": ["ROLE_USER", "ROLE_ADMIN"],
                    "description": "role is required and must be either ROLE_USER or ROLE_ADMIN"
                },
                "card":{
                    "bsonType": ["object", "null"],
                    "required": ["name", "imageUrl", "pin", "attempts"],
                    "properties": {
                        "name": {
                            "bsonType": "string",
                            "description": "name is required and must be string"
                        },
                        "imageUrl": {
                            "bsonType": "string",
                            "description": "imageUrl is required and must be string"  
                        },
                        "pin": {
                            "bsonType": "string",
                            "description": "pin is required and must be string"
                        },
                        "attempts": {
                            "bsonType": "int",
                            "minimum": 0,
                            "maximum": 3,
                            "description": "attempts is required and must be in [0, 3]"
                        }
                    }
                },
                "review": {
                    "bsonType": ["object", "null"],
                    "required": ["description", "rating", "createdAt"],
                    "properties": {
                        "description": {
                            "bsonType": "string",
                            "description": "description in review is required and must be string"
                        },
                        "rating": {
                            "bsonType": "int",
                            "minimum": 0,
                            "maximum": 5,
                            "description": "rating in review is required and must be in [0,5]"
                        },
                        "createdAt": {
                            "bsonType": "date",
                            "description": "createdAt in review is required and must be date"
                        }

                    }
                }
            }
        }
    }
});
db.countries.createIndex({"name": 1, "code": 1}, {"unique": true});
db.categories.createIndex({"name": 1}, {"unique": true});
db.likes.createIndex({"reviewUserId": 1, "userId": 1}, {"unique": true});
db.users.createIndex({"phone": 1}, {"unique": true});
db.countries.insertMany([
    {
        "name": "Polska",
        "code": "PL"
    },
    {
        "name": "Niemcy",
        "code": "DE"
    },
    {
        "name": "Chiny",
        "code": "CN"
    },
    {
        "name": "Czechy",
        "code": "CZ"
    },
    {
        "name": "Chorwacja",
        "code": "HR"
    },
    {
        "name": "Dania",
        "code": "DK"
    },
    {
        "name": "Finlandia",
        "code": "FI"
    },
    {
        "name": "Francja",
        "code": "FR"
    },
    {
        "name": "Grecja",
        "code": "GR"
    },
    {
        "name": "Hiszpania",
        "code": "ES"
    },
    {
        "name": "Japonia",
        "code": "JP"
    },
    {
        "name": "Indie",
        "code": "IN"
    },
    {
        "name": "Kanada",
        "code": "CA"
    },
    {
        "name": "Norwegia",
        "code": "NO"
    },
    {
        "name": "Rosja",
        "code": "RU"
    },
    {
        "name": "Stany zjednoczone",
        "code": "US"
    },
    {
        "name": "Szwecja",
        "code": "CH"
    },
    {
        "name": "Szwajcaria",
        "code": "SE"
    },
    {
        "name": "Turcja",
        "code": "TR"
    },
    {
        "name": "Węgry",
        "code": "HU"
    },
    {
        "name": "Wielka Brytania",
        "code": "GB"
    },
    {
        "name": "Włochy",
        "code": "IT"
    }
]);
