Dialect "modern"

Include "start.bb"

; sequence# | frames |	description 
; ---------	 ------   -----------
; 0				 72 	 	death
; 1 			 60 		idle	
; 2     		 27 	   jump
; 3 			 17			run_fast
; 4				 21 		run_left
; 5 			 21			run_right
; 6 			 23 		run_slow
; 7 			 36 		standard_walk
; 8 			 32 		walk
; 9 			 32 		walk_left
; 10 		    29			walk_right				 	  			

Local seq0,time0#,time0Step#	;base animation
Local seq1,time1#,time1Step#	;blend target
Local blend#, blendStep#
Local useBlend=1

seq0 = 1	;initially idle
time0Step = .02

CreateWindow GetDesktopWidth()/2,GetDesktopHeight()/2,"Animation Blender",WINDOW_FLAGS_CENTERED

camera = CreatePerspectiveCamera()
TurnEntity camera,-45,0,0
MoveEntity camera,0,0,-7.5

Local plane = CreatePlane(LoadMaterial("../../assets/materials/Ground037_1K-JPG"))

Local pmaterial = CreateEmissiveMaterial()
SetMaterialColor pmaterial,"emissive",1,1,1,1
Local pmesh = CreateSphereMesh(.05,8,4,pmaterial)
Local pmodel = CreateModel(pmesh)

light = CreateSpotLight()
SetEntityParent pmodel,light
MoveEntity light,0,2.5,0
SetLightRange light,5

TurnEntity light,-90,0,0

Local outerAngle# = 45 
Local innerAngle# = 0
SetLightShadowsEnabled light,True

model = LoadBonedModel("../../assets/models/base_male_animated.glb",True)
SetMeshShadowsEnabled GetModelMesh(model),True

While Not (PollEvents() And EVENT_MASK_CLOSE_CLICKED)

	If IsKeyHit(KEY_SPACE) useBlend = 1 - useBlend
	
	;Select new anim
	Local nseq=1
	If IsKeyDown(KEY_LEFT_SHIFT)
		;Run
		If IsKeyDown(KEY_UP)
			nseq=6
		ElseIf IsKeyDown(KEY_LEFT)
			nseq=5
		ElseIf IsKeyDown(KEY_RIGHT)
			nseq=4
		EndIf
	Else
		;Walk
		If IsKeyDown(KEY_UP)
			nseq=8
		ElseIf IsKeyDown(KEY_LEFT)
			nseq=10
		ElseIf IsKeyDown(KEY_RIGHT)
			nseq=9
		EndIf
	EndIf
	
	;Change to new anim
	If blendStep = 0 And seq0 <> nseq
		If useBlend
			time0Step = 0
			seq1 = nseq
			time1 = 0
			time1Step = 0.02
			blendStep = .1
		Else
			seq0 = nseq
			time0 = 0
			time0Step = 0.02
		EndIf
	EndIf
	
	;Update blend state
	blend = blend + blendStep
	If blend >= 1
		seq0 = seq1
		time0  = time1
		time0Step = time1Step
		time1 = 0
		time1Step = 0
		blend = 0
		blendStep = 0
	EndIf
	
	;Set base animation
	time0 = time0 + time0Step
	AnimateModel model,seq0,time0,2,1
	
	If blend>0 
		;Appy blend
		time1 = time1 + time1Step
		AnimateModel model,seq1,time1,2,blend
	EndIf
	
	If IsKeyHit(KEY_2)
		If outerAngle<90 outerAngle=outerAngle+3
	Else If IsKeyHit(KEY_1)
		If outerAngle>3 outerAngle=outerAngle-3
		If outerAngle<innerAngle innerAngle=outerAngle
	EndIf
	SetLightOuterConeAngle light,outerAngle
	
	If IsKeyHit(KEY_4)
		If innerAngle<outerAngle innerAngle=innerAngle+3
	Else If IsKeyHit(KEY_3)
		If innerAngle>0 innerAngle=innerAngle-3
	EndIf		
	SetLightInnerConeAngle light,innerAngle
	
	Clear2D()
	Draw2DText "Arrow keys/Shift to run. Using blend:"+useBlend+" (space to toggle)",0,0
	Draw2DText "Outer cone angle: "+outerAngle+" (1/2)",0,16
	Draw2DText "Inner cone angle: "+innerAngle+" (3/4)",0,32
	
	RenderScene()
	
	Present()
Wend 