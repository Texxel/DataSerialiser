# DataSerialiser

This is a library for serializing objects. It focuses on a easy to use API and 
forcing users to think about the serialised form of their objects. 

## Features
DataSerialiser has the following feauters:
* Simple API
* Magical circular reference resolvation
* Human readable serialized form
* Multiple different output forms possible (only Json output currently programed 
  in, but other forms are easy to add)
* Objects use a constructor for deserializing themselves (makes final fields soo
  much easier to work with)
* No consistency between usage of American and British English :)

## Usage
See the tests for usage. Someday, I'll write the wiki pages to explain it properly.
For now, I'll just give a breif overview:
- DataIn and DataOut are how classes convert themselves into data.
- DataIn/DataOut know how to convert themselves from/into simplified data called PData.
- A DataLoader (like JsonLoader) knows how to convert to/from the PData to a text file.
- In order for an object to be deserialized, it must impliment DataSerializable and
  either have a no arg constructor or a constructor that accepts a single DataIn object.
- Alternatively (for when you cannot change the class's code (such as serializing an 
  external library), then you can register a custom serializer into ConverterRegistry.
- The basic java class's (Class, all arrays, String, Collections and primitive wrappers)
  will work automagically.

## How the circular reference resolving works internally
How it works is it uses JNI to allocate a reference to an object without actaully
constructing the object. Then, if the object being constructed needs, say, a 
reference to its self to be constructed, then the allocated object can be passed
to itself in its constructor.

The relvant code is in com.github.texxel.data.serializers.ObjectCreator

## Using outside of LibGDX
DataSerialiser is built with the intension of being used with LibGDX. However, the
only dependencies on LibGDX are in the JsonLoader (for LibGDX's JsonWriter) and 
ObjectCreator (for LibGDX's SharedLibraryLoader). It should be fairly easy to adapt 
the code for use anywhere.
