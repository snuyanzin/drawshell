# Drawing shell

## Building & Start
### Prerequisites
  * Maven 3.2.5 or higher
  * Java 1.8 or higher
### Build      
```
mvn package
```
or
- on Linux
    ```
    ./mvnw package
    ```
- on Windows
    ```
    mvnw.bat package
    ```    
It will create `drawingshell-1.0-SNAPSHOT.jar`. 
### Start
There are several ways to start: 
- On Windows: `bin\drawingshell.bat` 
- On Linux `bin/drawingshell.sh`
- Just using java
  ```
   java -Xmx8G -jar drawingshell-1.0-SNAPSHOT.jar
  ``` 
  Memory settings are required to work with canvases up to 125000x125000 (but it could take a sufficient amount of time to have printed). In case you need much less sizes you can use lower memory settings.  
  
NOTE: here it is a piece of information which could help to choose suitable Xmx however on different platforms 
the values could differ so its better to test and pay attention to this table only as a start point. 
It also depends on number of colors in use. The table below is made for 3 colors in use. 

| Canvas size | Xmx |
--------------|-----|
| 80000 x 80000| 4Gb |
|100000 x 100000| 6Gb |
|125000 x 125000| 8Gb |
  
 
## Currently supported commands  
 
| Command | Description |
| ------- | ----------- |
| `H`         | Print this help. |
| `C w h`     | Create a new canvas of width w and height h. |
| `L x1 y1 x2 y2` | Draw a line through points `(x1, y1)` to `(x2, y2)`. Currently only horizontal and vertical lines are supported. Horizontal and vertical lines will be drawn using `x` character. |
| `L x1 y1 x2 y2 c` | The same as above but with color `c` specification. |
| `R x1 y1 x2 y2` | Draw a new rectangle, whose one corner is `(x1, y1)` and another is `(x2, y2)` using `x` character. |
| `R x1 y1 x2 y2 c` | The same as above but with color `c` specification. |
| `B x y c` | Alias for `B4`. |
| `B4 x y c` | Fill the entire area connected to `(x, y)` and having the same colour as `(x, y)` with colour `c`. 4-dots way of filling is used i.e. only side connections are taken into account while only corner connections will skipped. |
| `B8 x y c` | Fill the entire area connected to `(x, y)` and having the same colour as `(x, y)` with colour `c`. 8-dots way of filling is used i.e. both side and corner connections are taken into account. |
| `P` | Print current canvas. |
| `SET` | Show all existing properties with their values. |
| `SET p v` | Assign property `p` value `v`. |
| `Q` | Quit the program. |
                  
### Available properties (could be changed via `SET`)

| Property | Default value | Description |
| ------- | -------------- | ---------- |
| `hBorder` | `-` | Symbol for horizontal border. |
| `defaultDrawChar` | `x` | Default symbol to draw lines and rectangles. |
| `defaultEmptyChar` | ` ` | Default symbol to draw empty areas. |
| `showCanvasAfterCommand` | `true` | Print or not print canvas after draw command. |
| `vBorder` | &#124; | Symbol for vertical border. |

                        
## Demos

There could be 2 ways of working with drawing shell
1. There is support of input files (examples of input files could be found in `examples` folder) with commands i.e. the following command are possible
      - On Windows: 
        - `bin\drawingshell.bat examples\chessboard`
        - `bin\drawingshell.bat examples\maze`
        - `bin\drawingshell.bat examples\readme_example`
      - On Linux: 
        - `bin/drawingshell.sh examples/chessboard`
        - `bin/drawingshell.sh examples/maze`
        - `bin/drawingshell.sh examples/readme_example`
      - Just using java: 
        - `java -jar drawingshell-1.0-SNAPSHOT.jar <path_to_file>`
        
   this feature is also used in tests.
2. Interactive shell mode. Below there is a sample of it.

        create canvas or enter command: C 0 2 
        Usage: C <w> <h>. Where w and h must be in a range [1..2,147,483,647].
        In case of huge values be sure you have enough memory for jvm heap.
        
        create canvas or enter command: C 20 2 
        ----------------------
        |                    |
        |                    |
        ----------------------
        
        enter command: L 1 2 6 2
        ----------------------
        |                    |
        |xxxxxx              |
        ----------------------
        
        enter command: L 8 2 14 2 @
        ----------------------
        |                    |
        |xxxxxx @@@@@@@      |
        ----------------------
        
        enter command: R 16 1 20 3 *
        ----------------------
        |               *****|
        |xxxxxx @@@@@@@ *   *|
        ----------------------
        
        enter command: B 10 3 o
        Usage: B <x> <y> <c>. Where
        x must be an integer in a range [1..20] and
        y must be an integer in a range [1..2] and
        c must be a non-space and a non-control symbol from UTF-8 range.

        enter command: SET verticalBorder #
        enter command: SET horizontalBorder $
        
        enter command: B 10 1 o
        $$$$$$$$$$$$$$$$$$$$$$
        #ooooooooooooooo*****#
        #xxxxxxo@@@@@@@o*   *#
        $$$$$$$$$$$$$$$$$$$$$$
        enter command: Q

## Additional features and limitations

1. If there is no canvas exist then prompt is `create canvas: ` otherwise the prompt is `enter command: `.
2. Extra spaces are ignored that means that each of 2 commands:

   command1
   ```
   L 1 10 1 -5
   ```
   command2
   ```
                   L              1                   10            1               -5            
   ```
   will lead to the same result. While the second one looks awful it is still valid.
3. If a part of the line or rectangle specified via `L` or `R` commands is out of defined canvas then only the part matching to the canvas position/sizes will be drawn.
   Please have a look at demos section for more detailed.
4. If while `B` command there is specified a point which is out of canvas then nothing will be filled. Warning message will be shown. Please have a look at demos section for more detailed.
5. The commands with length of 1 Gb and more are not supported. If there is a requirement to have such feature supported it should be addressed to improvements section.
6. There is a hardcoded limit `Integer.MAX_VALUE` for canvas size (however there is an option to change it). Thus there are only environment limitations, please have a look at the table with memory required for different canvas sizes above. 
   At the same side for example Photoshop has limitation [30000](https://helpx.adobe.com/photoshop-elements/kb/maximum-image-size-limits-photoshop.html).
   Also there are some ideas to improve it (in case it is really required) in improvements section.

## Advanced features and possible customizations

1. There are lots of error handling messages, help and prompt definition in `DrawingShell.properties` file which could be customized.
2. In case of a new command is required there are 2 ways to define it:
   - By usage of `ru.nuyanzin.commands.ReflectiveCommandHandler` - please have a look at existing commands as example.
   - By usage of custom implementation of `ru.nuyanzin.commands.CommandHandler`.

## Suggestions for improvements (possible road map)

### General shell related suggestions
1. Properties (shell variables) support to keep some setting. For example it could be applied for
   - `BRIEF`, `VERBOSE` commands to set property which could be used to show/hide
     additional information/warnings while application work.
   - Lots of other settings like history file path, usage of highlighting or not.
   - Also being able to reset properties values to defaults.
2. Migration to use of jline3. It could provide history, history search, completion, highlighting,
   keymap, different events from keyboard and mouse handling support and lots of others features
   for more detailed please have a look at [jline3](https://github.com/jline/jline3/wiki).
   There is also a progress bar for long operations could be helpful (could be based on existing from jline3).
3. Being able to plug in new commands.
4. As resource bundle is already in use there could be added localization support for whatever required locales.
5. In case of having possibility so store results in files or whatever permanent storage
   it also would be nice to have possibility of saving part of heavy command work while it is in progress
   and to have a possibility to continue it from the last saved point (something similar to WAL files).
### Drawing related suggestions
1. Add more drawing related operations like any line (not only horizontal or vertical),
   circle, triangle, filled rectangles and other shapes, being able to fill not only area
   with the same but with the similar colours.
2. Being able to save the current state of canvas and load it later from the file or other source.
### Other
1. Currently there is a limitation of `Integer.MAX_VALUE` (and environment/jvm heap/etc.) for width or height of canvas.
   It could be increased with increasing memory but anyway it will require
   additional performance tuning. If it is really required to support
   huge canvas with faster speed it makes sense to think of
   - Currently there is a 2-dimensional array of `BitSet` is created based on width and height from the input. 
     As each row is a separate `BitSet` while a column contains data from different `BitSet` the row related operations are faster.
     Also as row is a single `BitSet` it consumes less memory. It makes sense to make row as a separate `BitSet` or column 
     as a separate `BitSet` based on whose length is larger.   
   - Like in case of an SQL engine some "statistics" about current color distribution
     could be gathered and then used on optimization level.
   - Off-heap storage, specific optimization algorithm and so on.
2. Huge commands (several gigabytes). May be support is not needed however what about protection from such commands?