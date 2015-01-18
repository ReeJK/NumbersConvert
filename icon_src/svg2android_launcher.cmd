:: drawable (ldpi and hdpi) - 72px
:: drawable-mdpi            - 48px
:: drawable-xhdpi           - 96px 
:: drawable-xxhdpi          - 144px
:: drawable-xxxhdpi         - 192px
:: (for GPlay)              - 512px 

@mkdir drawable
@mkdir drawable-mdpi
@mkdir drawable-xhdpi
@mkdir drawable-xxhdpi
@mkdir drawable-xxxhdpi

inkscape -z -y 0 -w  72 -h  72 -e drawable/ic_launcher.png %1
inkscape -z -y 0 -w  48 -h  48 -e drawable-mdpi/ic_launcher.png %1
inkscape -z -y 0 -w  96 -h  96 -e drawable-xhdpi/ic_launcher.png %1
inkscape -z -y 0 -w 144 -h 144 -e drawable-xxhdpi/ic_launcher.png %1
inkscape -z -y 0 -w 192 -h 192 -e drawable-xxxhdpi/ic_launcher.png %1
inkscape -z -y 0 -w 512 -h 512 -e google_play_icon.png %1
