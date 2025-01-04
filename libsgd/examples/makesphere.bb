Dialect "modern"

Include "start.bb"

; This Demo can create a Sphere from a lib and a sphere selfmade. 
; You can use it to create you own mesh 


Function createSphere(radius#, xSegs, ySegs, material)

	Local mesh = CreateMesh(0,1)

	If 1 = 0 Then
	
     vv0 = AddVertex (mesh , -1,-1,0,0,0,-1,0,1 )
     vv1 = AddVertex (mesh , 1,-1,0,0,0,-1,1,1 )
     vv2 = AddVertex (mesh , -1, 1,0,0,0,-1,0,0)
     vv3 = AddVertex (mesh , 1, 1,0,0,0,-1,1,0)
     surf2 = CreateSurface(mesh, material,0) 
     tri1 = AddTriangle (surf2,vv1,vv2,vv0) 
     tri2 = AddTriangle (surf2,vv3,vv2,vv1) 
	  Return mesh
	
	EndIf
	
	Local fxSegs# = 1/Float(xSegs), fySegs# = 1/Float(ySegs)
	
	For i=0 To xSegs-1
		AddVertex (mesh, 0, radius, 0, 0, 1, 0, (Float(i) + .5) * 2.0 * fxSegs, 0)
	Next
	
   For j = 1 To ySegs-1
	   Local pitch# = 90 - Float(j) * fySegs * 180 ;
	   For i = 0 To xSegs
		   Local yaw# = Float(i) * fxSegs * 360 ;
		   Local r# = Cos(pitch);
		   Local y# = Sin(pitch);
 		   Local x# = Cos(yaw) * r;
		   Local z# = Sin(yaw) * r; 
         AddVertex (mesh, x * radius, y * radius, z * radius, x, y, z, Float(i) * 2 * fxSegs, Float(j) * fySegs)
	   Next
   Next
	
   For i = 0 To xSegs-1
	   AddVertex (mesh, 0, -radius, 0, 0, -1, 0, (Float(i) + .5) * 2 * fxSegs, 1)
   Next
	
	Local surf = CreateSurface(mesh, material,0 );
	
	For i = 0 To xSegs-1
		AddTriangle (surf, i, i + xSegs, i + xSegs + 1)
	Next

   For j = 1 To ySegs-2
	   For i = 0 To xSegs-1
		   Local v0 = j * (xSegs + 1) + i - 1
		   Local v2 = v0 + xSegs + 2
		   AddTriangle (surf, v0, v2, v0 + 1)
		   AddTriangle (surf, v0, v2 - 1, v2)
     	Next
   Next
	For i = 0 To xSegs-1
		v0 = (xSegs + 1) * (ySegs - 1) + i - 1
		AddTriangle (surf, v0, v0 + xSegs + 1, v0 + 1)
	Next
	UpdateMeshTangents(mesh)
	Return mesh
	
End Function

CreateWindow GetDesktopWidth()/2,GetDesktopHeight()/2,"Custom mesh demo",WINDOW_FLAGS_CENTERED

env = LoadCubeTexture("../../assets/envmaps/sunnysky-cube.png", TEXTURE_FORMAT_ANY, TEXTURE_FLAGS_DEFAULT)

SetEnvTexture env

skybox = CreateSkybox(env)
SetSkyboxRoughness skybox, .3

light = CreateDirectionalLight()
TurnEntity light,-45,0,0	; Tilt light down 45 degrees 

;material = LoadPBRMaterial("../../assets/misc/test-texture.png")
material = LoadMaterial("../../assets/materials/Bricks076C_1K-JPG/")

SetMaterialFloat material, "roughness", .5

mesh1 = CreateSphereMesh(1,96,48,material) ; lib Sphere
mesh2 = CreateSphere(1,96,48,material)     ; selfmade sphere

model1=CreateModel(mesh1)
model2=CreateModel(mesh2)


MoveEntity model1,0,1.1,3
MoveEntity model2,0,-1.1,3

While Not PollEvents()

	If IsKeyDown(263)
		TurnEntity model1,0,3,0
		TurnEntity model2,0,3,0
	Else If IsKeyDown(262)
		TurnEntity model1,0,-3,0
		TurnEntity model2,0,-3,0
	EndIf
	
	If IsKeyDown(264)
		TurnEntity model1,3,0,0
		TurnEntity model2,3,0,0
	Else If IsKeyDown(265)
		TurnEntity model1,-3,0,0
		TurnEntity model2,-3,0,0
	EndIf
	
	RenderScene()
	
	Present()
	
Wend