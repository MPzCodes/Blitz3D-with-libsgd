Dialect "modern"

Include "start.bb"

Const NUM_BLOCKS = 25000
Const WORLD_SIZE = 100

Type Block
	Field model
	Field xr#,yr#
End Type

Type Bullet
	Field entity
	Field timeout
	Field vz#
End Type

Global bulletImage
Global slimeball

CreateWindow GetDesktopWidth()/2,GetDesktopHeight()/2,"スノー Blocks",WINDOW_FLAGS_CENTERED

CreateScene()

While Not (PollEvents() And EVENT_MASK_CLOSE_CLICKED)

	If IsKeyHit(KEY_ESCAPE)
		CreateScene()
	EndIf
	
	PlayerFly(.25)
	
	UpdateBullets()
	
	UpdateBlocks()
	
	Clear2D()
	Draw2DText "FPS:"+GetFPS(),0,0
	
	RenderScene()
	
	Present()
Wend

Function CreateScene() 

	Delete Each Bullet
	Delete Each Block
	ResetScene True
	
	Local env =  LoadCubeTexture("../../assets/envmaps/stormy-cube.jpg", TEXTURE_FORMAT_ANY, TEXTURE_FLAGS_DEFAULT)
	SetEnvTexture env
	
	Local skybox = CreateSkybox(env)
	
	bulletImage = LoadImage("../../assets/misc/light.png")

	Local light = CreateDirectionalLight()
	SetLightColor light,1,1,1,.2
	TurnEntity light,-60,0,0
	
	CreatePlayer(0)
	MoveEntity player,0,50,-100

	SetEntityName player,"Player"
	
	DebugLog player
	DebugLog FindEntityChild(0,"Player")
	
	light = CreateSpotLight()
	SetEntityParent light,player
	SetLightColor light,1,1,1,1
	SetLightRange light,50
	
	slimeball=LoadSound("../../assets/audio/slimeball.wav")
	
	CreateGround()

	CreateBlocks()
	
End Function

Function UpdateBlocks()

	For block:Block = Each Block
		TurnEntity block.model,block.xr,block.yr,0
	Next

End Function

Function UpdateBullets() 

	If IsKeyHit(KEY_SPACE)
		Local r#=Rnd(1), g# = Rnd(1), b#=Rnd(1)
		
		;Fake some NEON baby!
		Select(Rand(3))
		Case 1 r=1
		Case 2 g=1
		Case 3 b=1
		End Select
		
		Local bullet:Bullet = New Bullet
		
		bullet.entity = CreateSprite(bulletImage)
		SetSpriteColor bullet.entity,r,g,b,1
		
		SetEntityParent bullet.entity,player
		SetEntityParent bullet.entity,0
		
		bullet.timeout = 180
		bullet.vz = player_vz + .75
		If bullet.vz<.75 bullet.vz=.75
		
		Local light=CreatePointLight()
		SetLightShadowsEnabled light,True
		SetEntityParent light,bullet.entity
		SetLightColor light,r,g,b,2
		SetLightRange light,50
		
		PlaySound slimeball
	EndIf

	For bullet:Bullet = Each Bullet
		bullet.timeout = bullet.timeout - 1
		If bullet.timeout = 0
			DestroyEntity bullet.entity
			Delete bullet
		Else
			MoveEntity bullet.entity,0,0,bullet.vz
		EndIf
	Next

End Function

Function CreateGround()

	Local material = LoadPBRMaterial("../../assets/materials/Gravel023_1K-JPG")

	Local mesh = CreateBoxMesh(-WORLD_SIZE * 2,-1,-WORLD_SIZE*2,WORLD_SIZE*2,0,WORLD_SIZE*2,material)
	TransformTexCoords mesh,100,100,0,0

	Local model = CreateModel(mesh)
	
End Function


Function CreateBlocks()

	Local material = LoadPBRMaterial("../../assets/materials/Fabric048_1K-JPG")
	Local mesh = CreateBoxMesh(-1,-1,-1,1,1,1,material)
	SetMeshShadowsEnabled mesh,True
	
	For i = 1 To NUM_BLOCKS
		block:Block = New Block
		block.model = CreateModel(mesh)
		SetModelColor block.model, Rnd(1), Rnd(1), Rnd(1), 1
		SetEntityPosition block.model, Rnd(-WORLD_SIZE,WORLD_SIZE), Rnd(1,WORLD_SIZE), Rnd(-WORLD_SIZE,WORLD_SIZE)
		TurnEntity block.model, Rnd(360), Rnd(360), 0
		block.xr = Rnd(3)
		block.yr = Rnd(3)
	Next
	
End Function