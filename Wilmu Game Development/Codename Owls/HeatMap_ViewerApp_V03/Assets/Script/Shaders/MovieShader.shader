﻿Shader "Custom/Example"
{
    Properties
    {
        _Color ("Color", Color) = (1,1,1,1)
        _MainTex ("Albedo (RGB)", 2D) = "white" {}
        _AlphaVideo ("Alpha Video(R)", 2D) = "black" {}
        _Glossiness ("Smoothness", Range(0,1)) = 0.5
        _Metallic ("Metallic", Range(0,1)) = 0.0
    }
    SubShader
    {
    Tags { "Queue"="Transparent" "RenderType"="Transparent" }
        LOD 200
       
        CGPROGRAM
        // Physically based Standard lighting model, and enable shadows on all light types
        #pragma surface surf Standard alpha
 
        // Use shader model 3.0 target, to get nicer looking lighting
        #pragma target 3.0
 
        sampler2D _MainTex;
        sampler2D _AlphaVideo;
 
        struct Input {
            float2 uv_MainTex;
            float2 uv_AlphaVideo;
        };
 
        half _Glossiness;
        half _Metallic;
        fixed4 _Color;
 
        void surf (Input IN, inout SurfaceOutputStandard o) {
            // Albedo comes from a texture tinted by color
            fixed4 c = tex2D (_MainTex, IN.uv_MainTex) * _Color;
            fixed4 _alpha = tex2D (_AlphaVideo, IN.uv_AlphaVideo);
            o.Albedo = c.rgb;
            // Metallic and smoothness come from slider variables
            o.Metallic = _Metallic;
            o.Smoothness = _Glossiness;
            o.Alpha = _alpha.r;
           
        }
        ENDCG
    }
    FallBack "Diffuse"
}
 