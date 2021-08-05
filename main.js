Parse.Cloud.define("addRequest", async (request) => {
    const { objectId, newObjectId } = request.params;
    var User = Parse.Object.extend(Parse.User);
    var query = new Parse.Query(User);
    let result = await query.get(objectId, { useMasterKey: true });
    if (!result) new Error("No user found!");

    var newRequest = {
        "__type": "Pointer",
        "className": "_User",
        "objectId": newObjectId
      };

    result.add("requests", newRequest);
    try {
        result.save(null, { useMasterKey: true });
        return "User updated successfully!";
    } catch (e) {
        return e.message;
    }
});

Parse.Cloud.define("addFriend", async (request) => {
    const { objectId, newObjectId } = request.params;
    var User = Parse.Object.extend(Parse.User);
    var query = new Parse.Query(User);
    let result = await query.get(objectId, { useMasterKey: true });
    let user = await query.get(newObjectId, {useMasterKey: true});
    if (!result) new Error("No user found!");

    var newFriend = {
        "__type": "Pointer",
        "className": "_User",
        "objectId": newObjectId
      };

    result.add("friends", newFriend);
    result.remove("sentRequests", user);
    try {
        result.save(null, { useMasterKey: true });
        return "User updated successfully!";
    } catch (e) {
        return e.message;
    }
});

Parse.Cloud.define("deleteFriend", async (request) => {
    const { objectId, deleteFriendId } = request.params;
    var User = Parse.Object.extend(Parse.User);
    var query = new Parse.Query(User);
    let result = await query.get(objectId, { useMasterKey: true });
    let friend = await query.get(deleteFriendId, {useMasterKey: true});
    if (!result) new Error("No user found!");

    result.remove("friends", friend);
    try {
        result.save(null, { useMasterKey: true });
        return "User updated successfully!";
    } catch (e) {
        return e.message;
    }
});
