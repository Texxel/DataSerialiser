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
See the ![Wiki](https://github.com/Texxel/DataSerialiser/wiki)

## Using outside of LibGDX
DataSerialiser is built with the intension of being used with LibGDX. However, the
only dependencies on LibGDX are in the JsonLoader (for LibGDX's JsonWriter) and 
ObjectCreator (for LibGDX's SharedLibraryLoader). It should be fairly easy to adapt 
the code for use anywhere.
