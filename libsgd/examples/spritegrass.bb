;Use space to toggle sprite view modes.

Dialect "modern"

Include "start.bb"

CreateWindow GetDesktopWidth()/2,GetDesktopHeight()/2,"Sprite Grass",WINDOW_FLAGS_CENTERED

env = LoadCubeTexture("../../assets/envmaps/sunnysky-cube.png",TEXTURE_FORMAT_ANY,TEXTURE_FLAGS_DEFAULT)
SetEnvTexture env
skybox = CreateSkybox(env)

light = CreateDirectionalLight()
TurnEntity light,-45,0,0	; Tilt light down 45 degrees 

Local far#=300

CreatePlayer(0)
SetCameraFar camera, far
MoveEntity player,0,1.5,0

grassImage = LoadImage("../../assets/misc/grass1.png")
SetImageRect grassImage,-.5,0,.5,.5

Const n=50000

For i=0 To n

	sprite = CreateSprite(grassImage)
	
	TurnEntity(sprite,0,Rnd(360),0)
	MoveEntity(sprite,0,0,Rnd(far))
	
	Local sc#=Rnd(.1,1)
	ScaleEntity sprite,sc,sc,sc
Next

viewMode = 1 
SetImageViewMode grassImage,viewMode

While Not PollEvents()
	PlayerFly(.1)
	
	Clear2D()
	If IsKeyHit(KEY_SPACE)
		viewMode = viewMode + 1
		If viewMode=4 viewMode=1
		SetImageViewMode grassImage,viewMode
	EndIf
	
	Draw2DText "Sprite View mode:"+viewMode+" (space to toggle)",0,0
	
	RenderScene()
	Present()
Wend