conn = new Mongo();
db = conn.getDB("multipleCard");
db.createCollection("accounts", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "phone", "password", "email", "role", "isActive", "isBanned", "_class"],
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
                "email": {
                    "bsonType": "string",
                    "description": "email is required and must be string"
                },
                "role": {
                    "enum": ["ROLE_ADMIN", "ROLE_USER", "ROLE_SHOP"],
                    "description": "role is required and must be either of ROlE_SHOP, ROLE_USER and ROLE_ADMIN"
                },
                "isActive": {
                    "bsonType": "bool",
                    "description": "isActive is required and must be bool"
                },
                "isBanned": {
                    "bsonType": "bool",
                    "description": "isBanned is required and must be bool"
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
            "required": ["_id", "firstName", "lastName", "accountNumber", "name", "totalAmount", "imageUrl", "points", "_class"],
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
                "accountNumber": {
                    "bsonType": "string",
                    "description": "accountNumber is required and must be string"
                },
                "points": {
                    "bsonType": "array",
                    "minItems": 1,
                    "maxItems": 5,
                    "uniqueItems": true,
                    "items": {
                        "bsonType": "object",
                        "required": ["country", "city", "postalCode", "street", "houseNumber", "province"],
                        "additionalProperties": false,
                        "properties": {
                            "country": {
                                "bsonType": "string",
                                "description": "country is required and must be string"
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
                            },
                            "province": {
                                "bsonType": "string",
                                "description": "province is required and must be string"
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
            "required": ["_id", "name", "description", "imageUrl", "barcode", "categoryId", "amount", "shopId", "isActive", "_class"],
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
            "required": ["_id", "cardId", "productId", "createdAt", "isUsed", "amount", "_class"],
            "additionalProperties": false,
            "properties": {
                "_id": {
                    "bsonType": "objectId",
                    "description": "_id is required and must be objectId"
                },
                "cardId": {
                    "bsonType": "objectId",
                    "description": "cardId is required and must be objectId"
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
                    "required": ["country", "city", "postalCode", "street", "houseNumber", "province"],
                    "additionalProperties": false,
                    "properties": {
                        "country": {
                            "bsonType": "string",
                            "description": "country is required and must be string"
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
                        },
                        "province": {
                            "bsonType": "string",
                            "description": "province is required and must be string"
                        }
                    }
                },
                "points": {
                    "bsonType": "int",
                    "minimum": 0,
                    "description":
                        "points is required, must be int and greater or equal than 0"
                },
                "cardId":{
                    "bsonType": ["null", "objectId"],
                    "description": "cardId is required and must be objectId"
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
db.createCollection("cards", {
    "validator": {
        $jsonSchema: {
            "bsonType": "object",
            "required": ["_id", "userId", "name", "imageUrl", "pin", "attempts", "_class"],
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
                },
                "userId":{
                    "bsonType": "objectId",
                    "description": "userId is required and must be objectId"
                },
                "_class": {
                    "bsonType": "string",
                    "description": "_class is required and must be string",
                }
            }
        }}})
db.categories.createIndex({"name": 1}, {"unique": true});
db.likes.createIndex({"reviewUserId": 1, "userId": 1}, {"unique": true});
db.accounts.createIndex({"phone": 1}, {"unique": true});
db.accounts.createIndex({"email": 1}, {"unique": true});
db.users.createIndex({"review.description": "text"}, {"default_language": "none"})
db.shops.createIndex({"name": 1}, {"unique": true});
db.shops.createIndex({"accountNumber": 1}, {"unique": true});