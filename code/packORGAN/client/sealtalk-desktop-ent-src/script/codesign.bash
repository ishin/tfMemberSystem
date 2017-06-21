#codesign --verbose --deep --force --sign "Developer ID Application: Beijing Rong Cloud Network Technology CO., LTD (CQJSB93Y3D)" build/SealTalk_Ent-darwin-x64/SealTalk_Ent.app
codesign --verbose --deep --force --sign "Developer ID Application: Beijing Rong Cloud Network Technology CO., LTD (CQJSB93Y3D)" "build/"$1"-darwin-x64/"$1".app"
#for args in $@
#do
#        echo $args
#done
#echo $1