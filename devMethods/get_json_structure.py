import json
from collections import defaultdict

def merge(items):
    def _(item):
        return item[0] if lists else item
    
    def ret(values):
        return merge(values) if len(values) > 0 else None
    
    def wrap(ret_val):
        # indicating lists in the output JSON still doesn't quite work.
        # Seems to put some where they don't belong sometimes...
        #   -> Looks like sometimes the "in list" trait gets applied to all subobjects instead of to the parent object? (see pokemonCondition/unlockCondition in combatLeague)
        return [ret_val] if lists else ret_val

    if len(items) == 1: return items[0]

    partition = defaultdict(list)
    lists = items and isinstance(items[0], list)
    for item in map(_, items):
        if item is None: continue
        for key in item.keys():
            partition[key].append(item[key])
    return wrap({ key: ret(values) for key, values in partition.items() } or None)

def get_keys(json):
    if not isinstance(json, dict): return None # Base case for list of objects
    # When dealing with objects
    keys = dict()
    for key in json.keys():
        if isinstance(json[key], dict): # Nested object
            keys[key] = get_keys(json[key])
        elif isinstance(json[key], list): # List of stuff
            keys[key] = [merge([get_keys(item) for item in json[key]])]
        else: # String/number/etc
            keys[key] = None
    return keys or None

with open('..\\V2_GAME_MASTER.json', "r") as gm:
#with open('..\\test.json', "r") as gm:
    data = json.loads(gm.read())

parsed = get_keys(data)
with open("..\\parsed.json", "w") as f:
    f.write(json.dumps(parsed, indent=2))
