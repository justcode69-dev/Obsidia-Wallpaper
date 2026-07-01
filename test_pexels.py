import urllib.request
import json

req = urllib.request.Request("https://api.pexels.com/v1/curated?page=1&per_page=1")
req.add_header("Authorization", "68qUH52n7Y8JEmdNxgY7LDrg71F4pGk2rBS8jj8VjaSgdn0PLrUExnND")

try:
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode())
        print(json.dumps(data, indent=2))
except Exception as e:
    print(e)
