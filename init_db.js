conn = new Mongo();
db = conn.getDB("multipleCard");
db.createUser({
    "user": "user",
    "pwd": "user",
    "roles": ["readWrite"]
});
db.createCollection("accounts", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "phone", "password", "role", "isActive", "isShop", "_class"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "phone": {
                    "bsonType": "string",
                    "description": "login is required and must be string"
                },
                "password": {
                    "bsonType": "string",
                    "description": "password is required and must be string"
                },
                "role": {
                    "enum": ["ROLE_ADMIN", "ROLE_USER", "ROLE_SHOP"],
                    "description": "role is required and must be either of ROlE_SHOP, ROLE_USER and ROLE_ADMIN"
                },
                "isActive": {
                    "bsonType": "bool",
                    "description": "isActive is required and must be bool"
                },
                "isShop": {
                    "bsonType": "bool",
                    "description": "isShop is required and must be bool"
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
                }

            }
        }
    }
});
db.createCollection("shops", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "name", "totalAmount", "imageUrl", "points", "_class"],
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
                "points": {
                    "bsonType": "array",
                    "minItems": 1,
                    "maxItems": 5,
                    "uniqueItems": true,
                    "items": {
                        "bsonType": "object",
                        "required": ["countryId", "city", "postalCode", "street", "houseNumber"],
                        "additionalProperties": false,
                        "properties": {
                            "countryId": {
                                "bsonType": "objectId",
                                "description": "countryId is required and must be objectId"
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
                                "bsonType": "string",
                                "description": "houseNumber is required, must be string"
                            },
                            "apartmentNumber": {
                                "bsonType": ["int", "null"],
                                "minimum": 1,
                                "description": "apartmentNumber should be int or null and must be greater than 0"
                            }
                        }
                    }

                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
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
            "required": ["_id", "name", "description", "imageUrl", "barcode", "categoryId", "amount", "shopId", "isActive", "promotion", "_class"],
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
                },
                "isActive": {
                    "bsonType": "bool",
                    "description": "isActive is required and must be bool"
                },
                "promotion": {
                    "bsonType": ["int", "null"],
                    "minimum": 0,
                    "description": "amount must be greater or equal 0"
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
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
            "required": ["_id", "userId", "productId", "createdAt", "isUsed", "amount", "_class"],
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
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
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
            "required": ["_id", "name", "_class"],
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
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
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
            "required": ["name", "code", "_id", "_class"],
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
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
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
            "required": ["_id","reviewUserId", "userId", "_class"],
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
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
                }
            }
        }
    }
});
db.createCollection("users", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "firstName", "lastName", "points", "address", "_class"],
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
                "address": {
                    "bsonType": "object",
                    "required": ["countryId", "city", "postalCode", "street", "houseNumber"],
                    "additionalProperties": false,
                    "properties": {
                        "countryId": {
                            "bsonType": "objectId",
                            "description": "countryId is required and must be objectId"
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
                            "bsonType": "string",
                            "description": "houseNumber is required, must be string"
                        },
                        "apartmentNumber": {
                            "bsonType": ["int", "null"],
                            "minimum": 1,
                            "description": "apartmentNumber should be int or null and must be greater than 0"
                        }
                    }
                },
                "points": {
                    "bsonType": "int",
                    "minimum": 0,
                    "description":
                        "points is required, must be int and greater or equal than 0"
                },
                "card":{
                    "bsonType": ["object", "null"],
                    "required": ["name", "imageUrl", "pin", "attempts"],
                    "additionalProperties": false,
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
                    "additionalProperties": false,
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
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
                }
            }
        }
    }
});
db.countries.createIndex({"name": 1, "code": 1}, {"unique": true});
db.categories.createIndex({"name": 1}, {"unique": true});
db.likes.createIndex({"reviewUserId": 1, "userId": 1}, {"unique": true});
db.accounts.createIndex({"phone": 1}, {"unique": true});
db.countries.insertMany([
    {
        "name": "Polska",
        "code": "PL",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Niemcy",
        "code": "DE",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Chiny",
        "code": "CN",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Czechy",
        "code": "CZ",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Chorwacja",
        "code": "HR",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Dania",
        "code": "DK",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Finlandia",
        "code": "FI",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Francja",
        "code": "FR",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Grecja",
        "code": "GR",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Hiszpania",
        "code": "ES",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Japonia",
        "code": "JP",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Indie",
        "code": "IN",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Kanada",
        "code": "CA",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Norwegia",
        "code": "NO",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Rosja",
        "code": "RU",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Stany zjednoczone",
        "code": "US",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Szwecja",
        "code": "CH",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Szwajcaria",
        "code": "SE",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Turcja",
        "code": "TR",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Węgry",
        "code": "HU",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Wielka Brytania",
        "code": "GB",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    },
    {
        "name": "Włochy",
        "code": "IT",
        "_class": "com.thepapiok.multiplecard.collections.Country"
    }
]);
