:: drawable (ldpi and hdpi) - 48px
:: drawable-mdpi            - 32px
:: drawable-xhdpi           - 64px 
:: drawable-xxhdpi          - 96px
:: drawable-xxxhdpi         - 128px

@mkdir drawable
@mkdir drawable-mdpi
@mkdir drawable-xhdpi
@mkdir drawable-xxhdpi
@mkdir drawable-xxxhdpi

inkscape -z -y 0 -w  48 -h  48 -e drawable/ic_menu_%~n1.png %1
inkscape -z -y 0 -w  32 -h  32 -e drawable-mdpi/ic_menu_%~n1.png %1
inkscape -z -y 0 -w  64 -h  64 -e drawable-xhdpi/ic_menu_%~n1.png %1
inkscape -z -y 0 -w  96 -h  96 -e drawable-xxhdpi/ic_menu_%~n1.png %1
inkscape -z -y 0 -w 128 -h 128 -e drawable-xxxhdpi/ic_menu_%~n1.png %1
