# Proto JS plugin

The Gradle plugin which assists the Protobuf JS compiler in code generation.

Currently, the plugin only purpose is adding `fromJson(json)` and `fromObject(object)` methods to 
the generated messages. These methods allow to parse the message from the JSON `string` and 
JavaScript `Object` respectively.

## Usage

To use the plugin, add it to the `classpath` configuration of the `buildscript` as follows:

```groovy
buildscript {
    dependencies {
        classpath "io.spine.tools:spine-proto-js-plugin:$spineBaseVersion"
    }
}
```

The plugin may then be applied where necessary:

```groovy
apply plugin: "io.spine.tools.proto-js-plugin"
```

## Required configurations

At the moment, the plugin relies on the set of hard-coded Gradle configurations which are required 
to be set in the project applying the plugin.

These settings are:

1. The root of the JavaScript code generated by the Protobuf JS compiler set to 
   `${projectDir}/proto/${sourceSet}/js`.
2. The Protobuf descriptor set file stored at location 
   `${projectDir}/build/descriptors/${sourceSet}/known_types.desc`.
3. The `CommonJS` import style for the generated JavaScript files 
   (`js {option "import_style=commonjs"}` in `protobuf` extension).
4. The tasks `compileProtoToJs` and `copyModuleSources` available in the project and being the 
   dependencies of the `build` task.

In general, these settings mimic how the [Spine Web](https://github.com/SpineEventEngine/web) 
builds its JS modules.