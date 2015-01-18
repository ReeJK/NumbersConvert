@mkdir drawable
@mkdir drawable-mdpi
@mkdir drawable-xhdpi
@mkdir drawable-xxhdpi
@mkdir drawable-xxxhdpi

inkscape -z -y 0 -w 32 -h 24 -e drawable/ic_%~n1.png %1
inkscape -z -y 0 -w 24 -h 16 -e drawable-mdpi/ic_%~n1.png %1
inkscape -z -y 0 -w 48 -h 32 -e drawable-xhdpi/ic_%~n1.png %1
inkscape -z -y 0 -w 64 -h 48 -e drawable-xxhdpi/ic_%~n1.png %1
inkscape -z -y 0 -w 96 -h 64 -e drawable-xxxhdpi/ic_%~n1.png %1
