-keepattributes *Annotation*,Signature,EnclosingMethod,InnerClasses

-keepclassmembers class gr.ihu.eparousiologio.model.** {
    public <init>();
    <fields>;
    *** get*(...);
    void set*(...);
}
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName *;
    @com.google.firebase.firestore.Exclude *;
}

-keep class gr.ihu.eparousiologio.model.** { *; }

-keep class gr.ihu.eparousiologio.util.ExcelExporter { *; }
-keep class gr.ihu.eparousiologio.util.ExcelExporter$** { *; }

-keep class gr.ihu.eparousiologio.repository.AttendanceRepository$AttendanceSnapshot { *; }

-keep class schemaorg_apache_xmlbeans.system.** { *; }
-keepnames class schemaorg_apache_xmlbeans.system.**

-keep class org.apache.xmlbeans.** { *; }
-dontwarn org.apache.xmlbeans.**

-keep class org.openxmlformats.schemas.spreadsheetml.x2006.main.** { *; }
-keep class org.openxmlformats.schemas.drawingml.x2006.** { *; }
-keep class org.openxmlformats.schemas.officeDocument.x2006.** { *; }
-keep class org.openxmlformats.schemas.relationships.** { *; }

-keep class org.apache.poi.ooxml.** { *; }
-keep class org.apache.poi.openxml4j.** { *; }

-dontwarn javax.xml.stream.**
-dontwarn javax.activation.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.logging.log4j.**
-dontwarn com.sun.**

############################################
# OPTIONAL: DIAGNOSTICS (άνοιξέ τα αν ξαναδείς crash)
############################################
# -whyareyoukeeping class gr.ihu.eparousiologio.util.ExcelExporter
# -printusage build/outputs/mapping/usage.txt

-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTBandFmts
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTBubbleScale
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbl
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTDTable
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTDispUnits
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTExtension
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTLblOffset
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTMultiLvlStrRef
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotSource
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTProtection
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTSizeRepresents
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTSkip
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTStockChart
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTStyle
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTTextLanguageID
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTTimeUnit
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.CTUpDownBars
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STFirstSliceAng
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerSize
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STPageSetupOrientation
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STPerspective
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STRotX
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STRotY
-dontwarn org.openxmlformats.schemas.drawingml.x2006.chart.STThickness
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaBiLevelEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaCeilingEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaFloorEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaInverseEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaModulateEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaOutsetEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAlphaReplaceEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTAngle
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBackdrop
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBevel
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBiLevelEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBlendEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBlurEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTBoolean
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTCamera
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTCell3D
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTColorChangeEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTColorReplaceEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTColorSchemeList
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTConnectorLocking
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTCustomColorList
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTEffectReference
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTFillEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTFillOverlayEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTFlatText
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTGammaTransform
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTGlowEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTGrayscaleTransform
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTGroupLocking
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTHSLEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTHeaders
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTInnerShadowEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTInverseGammaTransform
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTInverseTransform
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTLightRig
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTLuminanceEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTObjectStyleDefaults
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTPresetShadowEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTPresetTextShape
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTReflectionEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeOffsetEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTShapeLocking
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTSoftEdgesEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTSupplementalFont
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTableBackgroundStyle
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTableCellBorderStyle
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineFillGroupWrapper
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTextUnderlineLineFollowText
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTintEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.CTTransformEffect
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.STEffectContainerType
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.STFixedAngle
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.STPresetMaterialType
-dontwarn org.openxmlformats.schemas.drawingml.x2006.main.STTextColumnCount
-dontwarn org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTRel
-dontwarn org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapThrough
-dontwarn org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTight
-dontwarn org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTWrapTopBottom
-dontwarn org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignH
-dontwarn org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.STAlignV
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTArray
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTEmpty
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTNull
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.CTVstream
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STCy
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STError
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes.STVectorBaseType
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTAcc
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTBar
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTBorderBox
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTBox
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTChar
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTEqArr
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTF
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTFunc
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTGroupChr
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTLimLow
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTLimUpp
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTNary
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathArgPr
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTOMathParaPr
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTOnOff
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTPhant
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTRPR
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTRad
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTSPre
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTSSubSup
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTSSup
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTShp
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTSpacingRule
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTUnSignedInteger
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.math.CTYAlign
-dontwarn org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STConformanceClass
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBoolean
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCacheHierarchies
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedItems
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalculatedMembers
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellStyles
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartFormat
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCommentPr
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConditionalFormat
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTConsolidation
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControlPr
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataBinding
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDataRefs
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDateTime
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDdeLink
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDimensions
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTError
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFieldGroup
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFilterColumn
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFormat
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTFunctionGroup
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTGradientFill
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHierarchyUsage
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTI
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMRUColors
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureDimensionMaps
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMeasureGroups
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMissing
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumber
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTObjectPr
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleLink
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPCDKPIs
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotFilter
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotHierarchy
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotSelection
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRecord
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTScenarios
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagPr
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTagTypes
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSmartTags
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTString
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTupleCache
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishObjects
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.CTX
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STComments
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STDataValidationImeMode
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STOleUpdate
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticAlignment
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STPivotAreaType
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STShowDataAs
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortBy
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STSortMethod
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableType
-dontwarn org.openxmlformats.schemas.spreadsheetml.x2006.main.STTargetScreenSize
-dontwarn org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlDel
-dontwarn org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMathCtrlIns
