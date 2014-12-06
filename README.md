java-morguefile-api
===================

Java Class for accessing the API of [morguefile.com](https://www.morguefile.com)

### Example usage: ###

```java
Morguefile mf_api = new Morguefile("YOUR_ID", "YOUR_SECRET");
String morgueResponse = mf_api.call("archive/search/new/1/cats", Morguefile.MF_API_JSON);

JsonParser jsonParser = new JsonParser();
JsonElement root = jsonParser.parse(morgueResponse);
JsonArray images = root.getAsJsonObject().get("response")
                    .getAsJsonObject().get("doc").getAsJsonArray();

for(int i = 0; i < images.size(); i++){
  String image_url = images.get(i).getAsJsonObject()
                      .get("Archive").getAsJsonObject()
                      .get("file_path_small").getAsString();
  System.out.println(image_url);
}
```
