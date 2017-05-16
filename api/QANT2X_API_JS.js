/**
 * Created by Lamhm on 4/13/2017.
 * https://www.npmjs.com/package/bytebuffer
 */
QANT2X = {};
QANT2X.DataType = {
    NULL: 0,
    BOOL: 1,
    BYTE: 2,
    SHORT: 3,
    INT: 4,
    LONG: 5,
    FLOAT: 6,
    DOUBLE: 7,
    UTF_STRING: 8,
    BOOL_ARRAY: 9,
    BYTE_ARRAY: 10,
    SHORT_ARRAY: 11,
    INT_ARRAY: 12,
    LONG_ARRAY: 13,
    FLOAT_ARRAY: 14,
    DOUBLE_ARRAY: 15,
    UTF_STRING_ARRAY: 16,
    QANT_ARRAY: 17,
    QANT_OBJECT: 18
}

QANT2X.DataType.Name = {};
for (var key in QANT2X.DataType) {
    QANT2X.DataType.Name[QANT2X.DataType[key]] = key;
}

QANT2X.SystemRequest = {
    Handshake: 0,
    Login: 1,
    Logout: 2,
    GetRoomList: 3,
    JoinRoom: 4,
    AutoJoin: 5,
    CreateRoom: 6,
    GenericMessage: 7,
    ChangeRoomName: 8,
    ChangeRoomPassword: 9,
    ObjectMessage: 10,
    CallExtension: 13,
    LeaveRoom: 14,
    SpectatorToPlayer: 17,
    PlayerToSpectator: 18,
    PublicMessage: 20,
    PrivateMessage: 21,
    AdminMessage: 23,
    KickUser: 24,
    BanUser: 25,
    FindRooms: 27,
    FindUsers: 28,
    PingPong: 29
}

QANT2X.SystemEvent = {};
for (var key in QANT2X.SystemRequest) {
    QANT2X.SystemEvent[QANT2X.SystemRequest[key]] = key;
}


QANT2X.createExtensionMessage = function (cmdName, extParams) {
    var params = new QAntObject();
    params.putUtfString("c", cmdName);
    params.putQAntObject("p", extParams);

    var object = new QAntObject();
    object.putByte("c", 1);
    //13 la send trong extension SystemRequest
    object.putShort("a", 13);
    object.putQAntObject("p", params);
    return object;
}

QANT2X.createSystemMessage = function (cmdId, params) {
    var object = new QAntObject();
    object.putByte("c", 0);
    object.putShort("a", cmdId);
    object.putQAntObject("p", params);
    return object;
}

QANT2X.sendExtensionMessage = function (cmdName, params) {
    MessageManager.send_v2(this.createExtensionMessage(cmdName, params));
}

QANT2X.sendSystemMessage = function (cmdId, params) {
    MessageManager.send_v2(this.createSystemMessage(cmdId, params));
}

QANT2X.QAntDataSerializer = {

    obj2bin: function (qAntObject, dataStream) {
        var keys = qAntObject.getKeys();
        for (var index in  keys) {
            var key = keys[index];
            var wrapper = qAntObject.get(key);
            var dataObj = wrapper.getObject();
            this.encodeQAntObjectKey(dataStream, key);
            this.encodeObject(dataStream, wrapper.getType(), dataObj);
        }

        return dataStream.getBytes();
    },

    binary2object: function (data) {
        if (data.length < 3) {
            console.log("[QANT2X_API] [ERROR] Can't decode an CASObject. Byte data is insufficient. Size: " + data.length + " bytes");
            return null;
        }
        var dataStream = new DataStream(data.length, data);
        return this.decodeQAntObject(dataStream);
    },

    decodeQAntObject: function (buffer) {
        var dataType = buffer.readByte();
        if (dataType != QANT2X.DataType.QANT_OBJECT) {
            console.log("[QANT2X_API] [ERROR] Invalid CASDataType. Expected: " + QANT2X.DataType.QANT_OBJECT
            + ", found: " + dataType);
            return null;
        }

        return this.decodeQAntObjectNotCheckType(buffer);
    },

    decodeQAntObjectNotCheckType: function (buffer) {
        var obj = new QAntObject();
        var size = buffer.readShort();
        if (size < 0) {
            console.log("[QANT2X_API] [ERROR] Can't decode QAntObject. Size is negative = " + size);
            return null;
        }

        for (var i = 0; i < size; ++i) {
            var keySize = buffer.readShort();
            if (keySize < 0 || keySize > 255) {
                console.log("[QANT2X_API] [ERROR] Invalid CASObject key length. Found = " + keySize);
                return null;
            }

            var keyData = buffer.readBytes(keySize);
            var key = String.fromCharCode.apply(String, keyData);
            var decodedObject = this.decodeObject(buffer);
            if (decodedObject == null) {
                console.log("[QANT2X_API] [ERROR] Could not decode value for key:  " + key);
                return null;
            }
            obj.put(key, decodedObject);
        }

        return obj;
    },

    bytes2String: function (bytes) {
        return String.fromCharCode.apply(String, bytes);
    },

    decodeObject: function (buffer) {
        var decodedObject;
        var dataType = buffer.readByte();
        if (dataType == QANT2X.DataType.NULL) {
            decodedObject = this.binDecode_NULL(buffer);
        } else if (dataType == QANT2X.DataType.BOOL) {
            decodedObject = this.binDecode_BOOL(buffer);
        } else if (dataType == QANT2X.DataType.UTF_STRING) {
            decodedObject = this.binDecode_UTF_STRING(buffer);
        } else if (dataType == QANT2X.DataType.LONG) {
            decodedObject = this.binDecode_LONG(buffer);
        } else if (dataType == QANT2X.DataType.INT) {
            decodedObject = this.binDecode_INT(buffer);
        } else if (dataType == QANT2X.DataType.FLOAT) {
            decodedObject = this.binDecode_FLOAT(buffer);
        }  else if (dataType == QANT2X.DataType.SHORT) {
            decodedObject = this.binDecode_SHORT(buffer);
        } else if (dataType == QANT2X.DataType.BYTE) {
            decodedObject = this.binDecode_BYTE(buffer);
        } else if (dataType == QANT2X.DataType.BYTE_ARRAY) {
            decodedObject = this.binDecode_BYTE_ARRAY(buffer);
        } else if (dataType == QANT2X.DataType.SHORT_ARRAY) {
            decodedObject = this.binDecode_SHORT_ARRAY(buffer);
        } else if (dataType == QANT2X.DataType.INT_ARRAY) {
            decodedObject = this.binDecode_INT_ARRAY(buffer);
        } else if (dataType == QANT2X.DataType.UTF_STRING_ARRAY) {
            decodedObject = this.binDecode_UTF_STRING_ARRAY(buffer);
        } else if (dataType == QANT2X.DataType.QANT_ARRAY) {
            if (dataType != QANT2X.DataType.QANT_ARRAY) {
                console.log("Can not decode array with QAntDataType ID:" + dataType);
                return null;
            }

            decodedObject = new QAntDataWrapper(QANT2X.DataType.QANT_ARRAY, this.decodeQAntArrayNotCheckType(buffer));
        } else {
            if (dataType != QANT2X.DataType.QANT_OBJECT) {
                console.log("Unknow QAntDataType ID:" + dataType);
                return null;
            }
            //ko dich trai 1 byte de kiem tra kieu
            decodedObject = new QAntDataWrapper(QANT2X.DataType.QANT_OBJECT, this.decodeQAntObjectNotCheckType(buffer));
        }

        return decodedObject;
    },

    binDecode_FLOAT: function(buffer){
        return new QAntDataWrapper(QANT2X.DataType.FLOAT, buffer.readFloat());
    },

    binDecode_NULL: function(buffer){
        return new QAntDataWrapper(QANT2X.DataType.NULL, null);
    },

    binDecode_BOOL: function (buffer) {
        return new QAntDataWrapper(QANT2X.DataType.BOOL, buffer.readByte() === 1);
    },

    binDecode_UTF_STRING_ARRAY: function (buffer) {
        var arraySize = buffer.readShort();
        if (arraySize < 0) {
            console.log("Error decoding typed array size. binDecode_UTF_STRING_ARRAY Negative size: " + arraySize);
            return null;
        }

        var array = new Array();
        for (var j = 0; j < arraySize; ++j) {
            var stringValue = this.binDecode_UTF_STRING(buffer);
            array.push(stringValue);
        }
        return new QAntDataWrapper(QANT2X.DataType.UTF_STRING_ARRAY, array);
    },

    binDecode_SHORT_ARRAY: function (buffer) {
        var arraySize = buffer.readShort();
        if (arraySize < 0) {
            console.log("Error decoding typed array size. binDecode_SHORT_ARRAY Negative size: " + arraySize);
            return null;
        }

        var array = new Array();
        for (var j = 0; j < arraySize; ++j) {
            var intValue = buffer.readShort();
            array.push(intValue);
        }
        return new QAntDataWrapper(QANT2X.DataType.SHORT_ARRAY, array);
    },

    binDecode_INT_ARRAY: function (buffer) {
        var arraySize = buffer.readShort();
        if (arraySize < 0) {
            console.log("Error decoding typed array size. binDecode_INT_ARRAY Negative size: " + arraySize);
            return null;
        }

        var array = new Array();
        for (var j = 0; j < arraySize; ++j) {
            var intValue = buffer.readInt();
            array.push(intValue);
        }
        return new QAntDataWrapper(QANT2X.DataType.INT_ARRAY, array);
    },

    binDecode_BYTE_ARRAY: function (buffer) {
        var arraySize = buffer.readInt();
        if (arraySize < 0) {
            console.log("Error decoding typed array size. Negative size: " + arraySize);
            return null;
        }

        var byteData = buffer.readBytesWithLength(arraySize);
        return new QAntDataWrapper(QANT2X.DataType.BYTE_ARRAY, byteData);
    },

    decodeQAntArrayNotCheckType: function (buffer) {
        var size = buffer.readShort();
        if (size < 0) {
            console.log("[QANT2X_API] [ERROR] Can't decode QAntArrayObject. Size is negative = " + size);
            return null;
        }

        var qAntArray = new QAntArrayObject();
        for (var i = 0; i < size; ++i) {
            var decodedObject = this.decodeObject(buffer);
            if (decodedObject == null) {
                console.log("Could not decode QAntSArray item at index: " + i);
                return null;
            }

            qAntArray.add(decodedObject);
        }

        return qAntArray;
    },

    binDecode_BYTE: function (buffer) {
        return new QAntDataWrapper(QANT2X.DataType.BYTE, buffer.readByte());
    },
    binDecode_SHORT: function (buffer) {
        return new QAntDataWrapper(QANT2X.DataType.SHORT, buffer.readShort());
    },

    binDecode_INT: function (buffer) {
        return new QAntDataWrapper(QANT2X.DataType.INT, buffer.readInt());
    },


    binDecode_LONG: function (buffer) {
        return new QAntDataWrapper(QANT2X.DataType.LONG, buffer.readLong());
    },

    binDecode_UTF_STRING: function (buffer) {
        var strLen = buffer.readShort();
        if (strLen < 0) {
            console.log("[QANT2X_API] [ERROR] Error decoding UtfString. Negative size: " + strLen);
            return null;
        }

        var strData = buffer.readBytes(strLen);
        return new QAntDataWrapper(QANT2X.DataType.UTF_STRING, (new TextDecoder("utf-8")).decode(strData));
    },

    encodeQAntObjectKey: function (dataStream, value) {
        dataStream.writeShort(value.length);
        dataStream.writeBytes(this.string2Bin(value));
    },

    encodeObject: function (dataStream, dataType, object) {
        switch (dataType) {
            case QANT2X.DataType.NULL:
                break;
            case QANT2X.DataType.BYTE:
                this.binEncode_BYTE(dataStream, object);
                break;
            case QANT2X.DataType.BOOL:
                this.binEncode_BOOL(dataStream, object);
                break;

            case QANT2X.DataType.SHORT:
                this.binEncode_SHORT(dataStream, object);
                break;

            case QANT2X.DataType.INT:
                this.binEncode_INT(dataStream, object);
                break;
            case QANT2X.DataType.UTF_STRING:
                this.binEncode_UTF_STRING(dataStream, object);
                break;
            case QANT2X.DataType.LONG:
                console.log("************** NOT SUPPORT LONG TYPE *********************")
                break;
            case QANT2X.DataType.BYTE_ARRAY:
                this.binEncode_BYTE_ARRAY(dataStream, object);
                break;
            case QANT2X.DataType.INT_ARRAY:
                this.binEncode_INT_ARRAY(dataStream, object);
                break;

            case QANT2X.DataType.UTF_STRING_ARRAY:
                this.binEncode_UTF_STRING_ARRAY(dataStream, object);
                break;

            case QANT2X.DataType.QANT_ARRAY:
                this.addData(dataStream, this.array2binary(object));
                break;
            case QANT2X.DataType.QANT_OBJECT:
                this.addData(dataStream, this.object2binary(object));
                break;


        }
    },

    binEncode_UTF_STRING_ARRAY: function (dataStream, value) {

    },

    binEncode_INT_ARRAY: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.INT_ARRAY);
        dataStream.writeShort(value.length);
        for (var i = 0; i < value.length; i++) {
            dataStream.writeInt(value[i]);
        }
    },

    binEncode_BYTE_ARRAY: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.BYTE_ARRAY);
        dataStream.writeInt(value.length);
        dataStream.writeBytes(value);
    },

    array2binary: function (qAntArrayObject) {
        console.log("************ array2binary ***********");
        var dataStream = new DataStream();
        dataStream.writeByte(QANT2X.DataType.QANT_ARRAY);
        dataStream.writeShort(qAntArrayObject.size());
        return this.arr2bin(qAntArrayObject, dataStream);
    },

    arr2bin: function (qAntArrayObject, dataStream) {
        for (var i = 0; i < qAntArrayObject.size(); i++) {
            var wrapper = qAntArrayObject.get(i);
            this.encodeObject(dataStream, wrapper.getType(), wrapper.getObject());
        }

        return dataStream.getBytes();

    },

    object2binary: function (qAntObject) {
        console.log("************ object2binary ***********");
        var dataStream = new DataStream();
        dataStream.writeByte(QANT2X.DataType.QANT_OBJECT);
        dataStream.writeShort(qAntObject.size());
        return this.obj2bin(qAntObject, dataStream);
    },

    binEncode_BYTE: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.BYTE);
        dataStream.writeByte(value);
    },
    binEncode_BOOL: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.BOOL);
        dataStream.writeByte(value == true ? 1 : 0);
    },
    binEncode_SHORT: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.SHORT);
        dataStream.writeShort(value);
    },

    addData: function (dataStream, newData) {
        dataStream.writeBytes(newData);
    },

    binEncode_INT: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.INT);
        dataStream.writeInt(value);
    },


    binEncode_UTF_STRING: function (dataStream, value) {
        dataStream.writeByte(QANT2X.DataType.UTF_STRING);
        dataStream.writeShort(value.length);
        dataStream.writeBytes(this.string2Bin(value));
    },


    string2Bin: function (str) {
        var result = [];
        for (var i = 0; i < str.length; i++) {
            result.push(str.charCodeAt(i));
        }
        return result;
    }

}

/* ---------------------- QAnt Data Wrapper ----------------------*/
function QAntDataWrapper(type, object) {

    this.type = type;
    this.object = object;
}
QAntDataWrapper.prototype.getType = function () {
    return this.type;
};

QAntDataWrapper.prototype.getTypeName = function () {
    return QANT2X.DataType.Name[this.type];
};

QAntDataWrapper.prototype.getObject = function () {
    return this.object;
};


/* ---------------------- QAnt Array Object ----------------------*/
function QAntArrayObject() {
    this.dataHolder = new Array();
}

QAntArrayObject.prototype.size = function () {
    return this.dataHolder.length;
}

QAntArrayObject.prototype.add = function (wrappedObject) {
    this.dataHolder.push(wrappedObject);
}

QAntArrayObject.prototype.get = function (index) {
    return this.dataHolder[index];
}

QAntArrayObject.prototype.addQAntObject = function (value) {
    this.addObject(value, QANT2X.DataType.QANT_OBJECT);
}

QAntArrayObject.prototype.addObject = function (value, typeId) {
    this.dataHolder.push(new QAntDataWrapper(typeId, value));
}

QAntArrayObject.prototype.dump = function () {
    var buffer = new StringBuilder();
    buffer.append('{');
    var objDump;
    for (var i = 0; i < this.size(); i++) {
        var wrappedObject = this.dataHolder[i];
        if (wrappedObject.getType() == QANT2X.DataType.QANT_OBJECT) {
            objDump = wrappedObject.getObject().dump();
        } else if (wrappedObject.getType() == QANT2X.DataType.QANT_ARRAY) {
            objDump = wrappedObject.getObject().dump();
        } else if (wrappedObject.getType() == QANT2X.DataType.BYTE_ARRAY) {
        } else {
            objDump = wrappedObject.getObject();
        }

        buffer.append(" (").append(wrappedObject.getTypeName().toLowerCase()).append(") ").append(objDump)
            .append(';');
    }

    buffer.append('}');
    return buffer.toPrettyHTMLString();

}

QAntArrayObject.prototype.dumpConsole = function () {
    var buffer = new StringBuilder();
    buffer.append('{');
    for (var key in this.dataHolder) {
        var wrapper = this.dataHolder[key];
        buffer.append("(").append(wrapper.getTypeName().toLowerCase()).append(") ").append(key).append(": ");
        if (wrapper.getType() == QANT2X.DataType.QANT_OBJECT) {
            buffer.append(wrapper.getObject().dumpConsole());
        } else if (wrapper.getType() == QANT2X.DataType.QANT_ARRAY) {
            buffer.append(wrapper.getObject().dumpConsole());
        } else if (wrapper.getType() == QANT2X.DataType.BYTE_ARRAY) {
        } else {
            buffer.append(wrapper.getObject());
        }
        buffer.append(';');
    }
    buffer.append('}');
    return buffer.toPrettyString();
}

QAntArrayObject.prototype.toJson = function () {
    var jsonObj = [];
    var obj;
    for (var i = 0; i < this.size(); i++) {
        var wrappedObject = this.dataHolder[i];
        if (wrappedObject.getType() == QANT2X.DataType.QANT_OBJECT) {
            obj = wrappedObject.getObject().toJson();
        } else if (wrappedObject.getType() == QANT2X.DataType.QANT_ARRAY) {
            obj = wrappedObject.getObject().toJson();
        } else if (wrappedObject.getType() == QANT2X.DataType.BYTE_ARRAY) {

        } else {
            obj = wrappedObject.getObject();
        }

        jsonObj.push(obj);
    }

    return jsonObj;
}


/* ---------------------- QAnt Object ----------------------*/

function QAntObject() {
    this.dataHolder = {};
}


QAntObject.prototype.putObj = function (key, value, dataType) {
    if (key == null) {
        console.log("[QANT_API]/////////////// key not null ///////////////");
        return;
    }

    if (key.length > 255) {
        console.log("[QANT_API]/////////////// keys must be less than 255 characters ///////////////");
        return;
    }

    if (value == null) {
        console.log("[QANT_API]/////////////// QAntObject requires a non-null value! If you need to add a null use the putNull() method. ///////////////");
        return;
    }

    if (value instanceof QAntDataWrapper) {
        this.dataHolder[key] = value;
    } else {
        this.dataHolder[key] = new QAntDataWrapper(dataType, value);
    }

}

QAntObject.prototype.toJson = function () {
    var jsonObj = {};
    for (var key in this.dataHolder) {
        var wrapper = this.dataHolder[key];
        if (wrapper.getType() == QANT2X.DataType.QANT_OBJECT) {
            jsonObj[key] = wrapper.getObject().toJson();
        } else if (wrapper.getType() == QANT2X.DataType.QANT_ARRAY) {
            jsonObj[key] = wrapper.getObject().toJson();
        } else if (wrapper.getType() == QANT2X.DataType.BYTE_ARRAY) {
        } else {
            jsonObj[key] = wrapper.getObject();
        }
    }

    return jsonObj;
}

QAntObject.prototype.putQAntArray = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.QANT_ARRAY);
}

QAntObject.newFromBinaryData = function (bytes) {
    return QANT2X.QAntDataSerializer.binary2object(bytes);
}

QAntObject.prototype.getCmdAction = function () {
    return this.getValue("a");
}

QAntObject.prototype.getTargetController = function () {
    return this.getValue("c");
}

QAntObject.prototype.getValue = function (key) {
    return this.get(key)["object"];
}


QAntObject.prototype.putBoolArray = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BOOL_ARRAY);
}

QAntObject.prototype.putBool = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BOOL);
}

QAntObject.prototype.putByte = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BYTE);
}

QAntObject.prototype.putByteArray = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BYTE_ARRAY);
}

QAntObject.prototype.putDouble = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.DOUBLE);
}

QAntObject.prototype.putInt = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.INT);
}

QAntObject.prototype.putNull = function (key) {
    this.dataHolder[key] = new QAntDataWrapper(QANT2X.DataType.NULL, null);
}

QAntObject.prototype.putQAntObject = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.QANT_OBJECT);
}

QAntObject.prototype.putShort = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.SHORT);
}

QAntObject.prototype.putUtfString = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.UTF_STRING);
}

QAntObject.prototype.put = function (key, wrappedObject) {
    this.putObj(key, wrappedObject, null);
}

QAntObject.prototype.get = function (key) {
    return this.dataHolder[key];
}

QAntObject.prototype.get = function (key) {
    return this.dataHolder[key];
}

QAntObject.prototype.putBool = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BOOL);
}

QAntObject.prototype.putBool = function (key, value) {
    this.putObj(key, value, QANT2X.DataType.BOOL);
}

QAntObject.prototype.toBinary = function () {
    return QANT2X.QAntDataSerializer.object2binary(this);
}


QAntObject.prototype.getKeys = function () {
    return Object.keys(this.dataHolder);
}

QAntObject.prototype.size = function () {
    var count = 0;
    for (var prop in this.dataHolder) {
        if (this.dataHolder.hasOwnProperty(prop)) {
            ++count;
        }
    }

    return count;
}

QAntObject.prototype.dump = function () {
    var buffer = new StringBuilder();
    buffer.append('{');
    for (var key in this.dataHolder) {
        var wrapper = this.dataHolder[key];
        buffer.append("(").append(wrapper.getTypeName().toLowerCase()).append(") ").append(key).append(": ");
        if (wrapper.getType() == QANT2X.DataType.QANT_OBJECT) {
            buffer.append(wrapper.getObject().dump());
        } else if (wrapper.getType() == QANT2X.DataType.QANT_ARRAY) {
            buffer.append(wrapper.getObject().dump());
        } else if (wrapper.getType() == QANT2X.DataType.BYTE_ARRAY) {
        } else {
            buffer.append(wrapper.getObject());
        }
        buffer.append(';');
    }
    buffer.append('}');
    return buffer.toPrettyHTMLString();

}

QAntObject.prototype.dumpConsole = function () {
    var buffer = new StringBuilder();
    buffer.append('{');
    for (var key in this.dataHolder) {
        var wrapper = this.dataHolder[key];
        buffer.append("(").append(wrapper.getTypeName().toLowerCase()).append(") ").append(key).append(": ");
        if (wrapper.getType() == QANT2X.DataType.QANT_OBJECT) {
            buffer.append(wrapper.getObject().dumpConsole());
        } else if (wrapper.getType() == QANT2X.DataType.QANT_ARRAY) {
            buffer.append(wrapper.getObject().dumpConsole());
        } else if (wrapper.getType() == QANT2X.DataType.BYTE_ARRAY) {
        } else {
            buffer.append(wrapper.getObject());
        }
        buffer.append(';');
    }
    buffer.append('}');
    return buffer.toPrettyString();
}

function StringBuilder() {
    var strings = [];

    this.append = function (string) {
        string = verify(string);
        if (string.length > 0) strings[strings.length] = string;
        return this;
    };

    this.appendLine = function (string) {
        string = verify(string);
        if (this.isEmpty()) {
            if (string.length > 0) strings[strings.length] = string;
            else return;
        }
        else strings[strings.length] = string.length > 0 ? "\r\n" + string : "\r\n";
    };

    this.clear = function () {
        strings = [];
    };

    this.isEmpty = function () {
        return strings.length == 0;
    };

    this.toString = function () {
        return strings.join("");
    };

    this.toPrettyString = function () {
        var rawDump = this.toString();
        var buf = new StringBuilder();
        var indentPos = 0;

        for (var i = 0; i < rawDump.length; ++i) {
            var ch = rawDump.charAt(i);
            if (ch == '{') {
                ++indentPos;
                buf.append("\n").append(this.getFormatConsoleTabs(indentPos));
            } else if (ch == '}') {
                if (--indentPos < 0) {
                    console.log("Argh! The indentPos is negative. TOKENS ARE NOT BALANCED!");
                }
                buf.append("\n").append(this.getFormatConsoleTabs(indentPos));
            } else if (ch == ';') {
                buf.append("\n").append(this.getFormatConsoleTabs(indentPos));
            } else {
                buf.append(ch);
            }
        }

        if (indentPos != 0) {
            console.log("Argh! The indentPos is not == 0. TOKENS ARE NOT BALANCED!");
        }

        return buf.toString();
    };
    this.toPrettyHTMLString = function () {
        var rawDump = this.toString();
        var buf = new StringBuilder();
        var indentPos = 0;

        for (var i = 0; i < rawDump.length; ++i) {
            var ch = rawDump.charAt(i);
            if (ch == '{') {
                ++indentPos;
                buf.append("<br>").append(this.getFormatTabs(indentPos));
            } else if (ch == '}') {
                if (--indentPos < 0) {
                    console.log("Argh! The indentPos is negative. TOKENS ARE NOT BALANCED!");
                }
                buf.append("<br>").append(this.getFormatTabs(indentPos));
            } else if (ch == ';') {
                buf.append("<br>").append(this.getFormatTabs(indentPos));
            } else {
                buf.append(ch);
            }
        }

        if (indentPos != 0) {
            console.log("Argh! The indentPos is not == 0. TOKENS ARE NOT BALANCED!");
        }

        return buf.toString();
    };

    this.getFormatTabs = function (howMany) {
        return this.strFill('&nbsp', howMany);
    };

    this.getFormatConsoleTabs = function (howMany) {
        return this.strFill('\t', howMany);
    };

    this.strFill = function (c, howMany) {
        var chars = [howMany];
        chars.fill(c);
        return chars.join();
    };

    var verify = function (string) {
        if (!defined(string)) return "";
        if (getType(string) != getType(new String())) return String(string);
        return string;
    };

    var defined = function (el) {
        // Changed per Ryan O'Hara's comment:
        return el != null && typeof(el) != "undefined";
    };

    var getType = function (instance) {
        if (!defined(instance.constructor)) throw Error("Unexpected object type");
        var type = String(instance.constructor).match(/function\s+(\w+)/);

        return defined(type) ? type[1] : "undefined";
    };
};


