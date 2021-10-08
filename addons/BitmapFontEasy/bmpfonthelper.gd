tool
extends BitmapFont

var index = 0

export(Array, String) var charset setget changeCharset;
export(Array, Texture) var textureSet setget changeTextureset;
export(Vector2) var globalOffset setget changeGlobalOffset;
export(float) var globalAdvance setget changeGlobalAdvance;
export(Dictionary) var letterSpecificOffset setget changeLetterSpecificOffset;
export(Dictionary) var letterSpecificAdvance setget changeLetterSpecificAdvance;

func changeGlobalOffset(value):
	globalOffset = value;
	update();
	
func changeGlobalAdvance(value):
	globalAdvance = value;
	update();

func changeLetterSpecificOffset(value):
	letterSpecificOffset = value;
	update();
	
func changeLetterSpecificAdvance(value):
	letterSpecificAdvance = value;
	update();

func changeCharset(value):
	charset = value;
	update();

func changeTextureset(value):
	textureSet = value;
	update()
	
func update():

	if(len(charset) != len(textureSet)):
		return print("Characters and textureSet count needs to be the same");

	var font = self
	font.clear();
	
	for i in range(len(charset)):
		if(len(charset[i]) == 0): 
			return print("Character #"+str(i)+" is empty");

		var tex = textureSet[i];
		if(tex == null):
			return print("Texture #"+str(i)+" is empty");
		
		var letter = charset[i][0];
		var chr = ord(letter);
		var w  = tex.get_width()
		var h  = tex.get_height()
		
		var offset = globalOffset;
		if(letterSpecificOffset.has(letter) && letterSpecificOffset[letter] is Vector2): 
			offset += letterSpecificOffset[letter];
			
		var advance = globalAdvance;
		if(letterSpecificAdvance.has(letter) && letterSpecificAdvance[letter] is float): 
			advance += letterSpecificAdvance[letter];
		
		font.add_texture(tex);
		font.add_char(chr, i, Rect2(0,0,w,h), offset, advance);
	
	print("Font updated")
