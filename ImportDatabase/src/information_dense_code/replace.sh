for filename in test_xml/*; do 
    [ -f "$filename" ] || continue
    mv $filename ${filename//.txt/}

done
