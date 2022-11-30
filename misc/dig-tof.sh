function digprint() {
	dig "$1" | grep '\bIN\b' | grep '\bA\b' | grep -v ';' | awk '{print $5 "\t" $1}'
}

for i in {1..34}
do
	digprint "jp-$i.toweroffantasy-global.com"
done

for i in {1..23}
do
	digprint "na-$i.toweroffantasy-global.com"
done

for i in {1..33}
do
	digprint "eu-$i.toweroffantasy-global.com"
done

for i in {1..19}
do
	digprint "sa-$i.toweroffantasy-global.com"
done

for i in {1..23}
do
	digprint "sea-$i.toweroffantasy-global.com"
done
