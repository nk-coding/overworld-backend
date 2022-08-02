#!/bin/bash

HOST="http://localhost"

TEMP="./.temp-dummy-data"


mkdir "$TEMP" || exit 1
cd "$TEMP"

for i in {1..15}; do
    curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
    --request POST "${HOST}/overworld/api/v1/courses" \
    --header "Content-Type: application/json" \
    --data-raw "{
    \"courseName\": \"dummy\"
    }"
done

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/1/minigame-tasks/1" \
--header "Content-Type: application/json" \
--data-raw "{
            \"id\": \"17712850-6956-410c-9b6f-0aa06e2c7f4e\",
            \"area\": {
                \"worldIndex\": 1,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"game\": \"kein game\",
            \"configurationId\": \"17712850-6956-410c-9b6f-0aa06e2c7f4e\"
        }"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/1/minigame-tasks/2" \
--header "Content-Type: application/json" \
--data-raw "       {
             \"id\": \"c5a40953-1d84-498f-9008-8a74129b734a\",
            \"area\": {
                \"worldIndex\": 1,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"game\": \"moorhuhn\",
            \"configurationId\": \"c5a40953-1d84-498f-9008-8a74129b734a\"
        }"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output npc1 --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/1/npcs/1" \
--header "Content-Type: application/json" \
--data-raw "{
            \"id\": \"4834f529-4e25-4b33-ad0d-dd2f8eb9d015\",
            \"areaLocation\": {
                \"worldIndex\": 1,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"text\": \"Hello GamifyIT! I am Coon and I like to eat berries and bad students.\"
        }"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output npc2 --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/1/npcs/2" \
--header "Content-Type: application/json" \
--data-raw "{
            \"id\": \"8063e750-0595-49d4-8651-684982957389\",
            \"areaLocation\": {
                \"worldIndex\": 1,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"text\": \"Howdy!\"
        }"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output npc3 --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/1/npcs/3" \
--header "Content-Type: application/json" \
--data-raw "{
            \"id\": \"b39c8ec1-bc8d-45b5-94f6-e0a160a59523\",
            \"areaLocation\": {
                \"worldIndex\": 1,
                \"dungeonIndex\": null
            },
            \"index\": 3,
            \"text\": \"Boo!! I am a ghost! Boo!\"
        }"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/courses/15/playerstatistics" \
--header "Content-Type: application/json" \
--data-raw "{
  \"userId\": \"1\",
  \"username\": \"Maik\"
}"

NPC_ID=$(jq -r .id npc1) 
curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/internal/submit-npc-pass" \
--header "Content-Type: application/json" \
--data-raw "{
  \"npcId\": \"${NPC_ID}\",
  \"completed\": true,
  \"userId\": \"1\"
}"

NPC_ID=$(jq -r .id npc2)
curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/internal/submit-npc-pass" \
--header "Content-Type: application/json" \
--data-raw "{
  \"npcId\": \"${NPC_ID}\",
  \"completed\": false,
  \"userId\": \"1\"
}"

NPC_ID=$(jq -r .id npc3)
curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/internal/submit-npc-pass" \
--header "Content-Type: application/json" \
--data-raw "{
  \"npcId\": \"${NPC_ID}\",
  \"completed\": false,
  \"userId\": \"1\"
}"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/internal/submit-game-pass" \
--header "Content-Type: application/json" \
--data-raw "{
  \"game\": \"kein game\",
  \"configurationId\": \"17712850-6956-410c-9b6f-0aa06e2c7f4e\",
  \"score\": 0,
  \"userId\": \"1\"
}"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request POST "${HOST}/overworld/api/v1/internal/submit-game-pass" \
--header "Content-Type: application/json" \
--data-raw "{
  \"game\": \"moorhuhn\",
  \"configurationId\": \"c5a40953-1d84-498f-9008-8a74129b734a\",
  \"score\": 100,
  \"userId\": \"1\"
}"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/2" \
--header "Content-Type: application/json" \
--data-raw "{
    \"id\": \"5aab0d50-697c-4d37-b1eb-91718edadc7c\",
    \"index\": 2,
    \"staticName\": \"Sunny Beach\",
    \"topicName\": \"\",
    \"active\": true,
    \"minigameTasks\": [
        {
            \"id\": \"6dc23f4f-e6fe-4889-9012-52a492a0116f\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 4,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"2fd46816-1ce2-4272-8188-651658a05a9e\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 9,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"effe66f4-2353-48c9-8112-0d78a6befc17\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 6,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"39069106-6855-430a-a2a9-4c64401a6d50\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 5,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"8613623b-4726-48fe-9ec2-a89aa2c5114d\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 11,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"3214ecdd-70a2-4364-8ee6-4d523bd7e904\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 7,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"828e20dd-6b2c-40b1-a91b-13105d0bbe3b\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"13c4a3d9-a838-46c2-97a9-844ff15378fc\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"c22598b7-0561-41f3-b780-7af8b80c0871\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 8,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"cb224d19-0a9b-42be-8e6f-9484bb6b6d52\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 12,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"0183e5e6-089e-4d47-baca-f0d17d023920\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 3,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"4f9bada0-14a6-4e59-98d7-ebbd1a563906\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 10,
            \"game\": null,
            \"configurationId\": null
        }
    ],
    \"npcs\": [
        {
            \"id\": \"1c058b24-2ce5-4c62-8226-3b55879299bc\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 8,
            \"text\": \"\"
        },
        {
            \"id\": \"55eba6a7-d591-463d-a91d-30d6a512685c\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"text\": \"\"
        },
        {
            \"id\": \"3598b9fe-ec63-4bb4-a4d6-d8dafa5888b3\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 6,
            \"text\": \"\"
        },
        {
            \"id\": \"1b4d8579-67af-4df5-9235-a3a1154d9b81\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 10,
            \"text\": \"\"
        },
        {
            \"id\": \"a39b1d1c-52d1-4c56-8c9b-33caf370a383\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 5,
            \"text\": \"\"
        },
        {
            \"id\": \"8eff5914-2ec4-4435-a059-6a88b4fbf7f9\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 3,
            \"text\": \"\"
        },
        {
            \"id\": \"447e0612-936b-4698-93e6-576cb6d7b9ad\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 7,
            \"text\": \"\"
        },
        {
            \"id\": \"b767a942-f719-45d5-b72f-3167378cfc2b\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 9,
            \"text\": \"\"
        },
        {
            \"id\": \"71ca909f-5e3a-4d11-964c-4419923fec12\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 4,
            \"text\": \"\"
        },
        {
            \"id\": \"a2052faf-92d7-48fc-8954-a3c6cdc3faee\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"text\": \"\"
        }
    ],
    \"dungeons\": [
        {
            \"id\": \"41baea76-4b67-4110-a776-db9a8d9146bd\",
            \"index\": 1,
            \"staticName\": \"dungeon1\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"108795c1-4638-49ef-ae95-63924ed69e48\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5c1bd815-f713-41a7-841e-73af0041364e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"7fa1293b-99e0-44df-bc05-21da9460285f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"67a218b4-69b2-4685-bbf3-0472ebbfdf5d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c1040e27-c90a-486e-8418-8610745fc030\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f7f77184-87e6-4a6c-b23a-25545f4b8d73\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5ccf0b56-430b-490c-a38d-3f2e1bc0c176\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"b3c6801e-a9c7-4706-aa38-cb7f0f07c874\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"03524828-000c-4bb9-a4b7-cd130f5d5fed\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"2b478558-3097-47c8-b83f-5118a389360f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"2e6a1616-7952-4897-a539-8b7579dca6a1\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"fd91f9ae-8cb5-4cc8-913a-fd7ec4503eb1\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"7209fe7d-7f8a-4bcf-b1f4-be1b55a2861b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"db30886f-292a-4aef-a8e0-917a4fa4106e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"9a540cfb-f857-4e66-90dc-e6ac48140579\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"31143cc3-fb0c-45d9-8924-660e41b45e71\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 9,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fa18493c-754f-4de2-92a9-ca75f4f4df04\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"ac730fdd-09a7-4610-a566-6ba7cdee41ea\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"61844013-1553-4cbd-9593-27619bc09b82\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"44cbf24e-e1ef-4538-82de-658adc65ec2f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"a2237e05-ec42-428b-bfea-c508b3b016b6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"d2952bc2-9a1c-457a-8d69-bb8756c9fa90\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 2,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"ea7cb4dd-e151-4ceb-b495-1b10c75c97ed\",
            \"index\": 2,
            \"staticName\": \"dungeon2\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"f2069609-b97b-4d96-a4e3-6ffbfc7d0835\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"323e75bc-b5b0-42af-90d1-165845b180e7\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"3382b8c9-cf82-4181-8cfe-e53887fa9010\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"8c8546c4-671d-4535-8aa3-0ec92949d73b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"954d4134-6362-4f59-afa8-6f9c3d13ce91\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"25250335-2cfd-4319-a0ec-3e781c739bfc\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f1073d8d-c6e9-4862-9ad9-67e1877e097f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f7681c5c-97f5-4b55-889a-c6b42b1e0dfe\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e2e74bd8-24ee-43ee-a67d-8ddc65f0e16b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"9892ee1a-ccd5-4799-931c-8b4b9729b9b2\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f638ced7-41b3-40f2-a6c8-4b4c9d744596\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"1853d4f7-6659-4475-87a2-7fa5af3f71b6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"d0d94ca3-6fe4-40b5-aeaa-e1569e82066e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 9,
                    \"text\": \"\"
                },
                {
                    \"id\": \"d87cfe8e-7713-4a42-ac6a-ee51145f7d0f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"0bd1dc76-0ed3-446c-9d25-4532b65f2dfb\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"06538435-abb0-45ea-8f0e-9a8154f94896\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c4001128-2561-4ca1-a93d-a8e9d3047037\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"51cdba16-5fad-44d4-a79c-094d4d2cdd67\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"dc4b5987-221b-4ed2-9999-a28686591880\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"3f3aeb96-7a9e-4809-b61c-d422cd588238\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"04ba88e5-2da2-4d85-822d-c0d21da9c749\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"1ec34f37-5a82-4175-9d07-2c29b4e43d09\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 7,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"8a86adfa-7cc1-4134-9a4c-cb056c395ab8\",
            \"index\": 3,
            \"staticName\": \"dungeon3\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"fbcf9a42-bf81-49fe-87b6-801ccb24f6f4\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"6af52a13-2f64-482e-b5bc-0bd0cc9f49a9\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"45dc2d59-1ac9-40e5-8374-87bc95398c45\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4585a4ff-4928-4ccc-812d-8e0b543fa3f3\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"95837ad5-a1f4-4596-85a7-517a03a11471\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"d27ee3d1-0bad-4572-810e-a355dc20d1ac\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c400a907-0547-4007-bc30-2ef7dd7c9063\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f16641a7-dcd4-4fe2-bead-965e6b5da40a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"ad969c27-b57d-4fee-a359-f6e4b17391da\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e3b65bee-dc2d-4c9f-8c78-e9e0d74ee60d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"427502e5-dafd-444d-97a0-38c1f4b244aa\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"0331b5b3-108a-4cd9-9fa8-68096cd693dd\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"715ac316-31f8-4911-ac36-2a2cb69ff288\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"21996270-2424-43f5-8b6c-69972772650d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"817e9521-8dc7-4c2e-921c-313bbcc6631a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"779911c7-1d43-4bce-9cbd-e3a692bea6a6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"57ff2b11-cdb1-4b15-bbe6-b1092d059399\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"cc33372e-0d99-4343-8ed8-c2f36db34b79\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"b878f2ba-009d-498e-a89e-08b6ec76e37b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"00863429-dd19-4bee-9b02-05dc12e6c647\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"671a94ee-02ff-41b2-b9fb-f6b670b5a454\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"ff0c0bd8-0870-42d0-af6b-824dcdde886e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 9,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"d7dbd5bf-4c17-4f73-a04a-0b0904fe9353\",
            \"index\": 4,
            \"staticName\": \"dungeon4\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"8fd56eaa-e602-427d-a22b-37bf79a30c2a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5ed550a8-906e-46a1-8260-ca62bef7d21b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"307c7366-3992-4ff7-95d4-d0cb2152ac36\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4e01a8a9-c70f-491f-9ed6-26a873ba6bf9\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c8b6926f-ef22-47cf-960d-9426afb3ba8b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5a8f7e12-c44e-4669-91e1-ee82e52c44cc\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e1658ed1-beee-4f69-98fb-5eb0fe3c90de\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5e1eafba-86be-4c2f-a2e5-7136f21b39df\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"b7421b34-3fc1-445a-acb7-dcbc26423e27\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4833e957-9170-44ba-8b88-787bef2564e5\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"d126c5d0-590e-4a8d-afda-c0832ea2756d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"0029289d-16be-4d4e-aa88-fb2703e02630\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"0a5ee458-0050-4720-8dde-78cdbbcce1e8\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"b7466f9f-0866-427f-b8fd-5e45f32763c0\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fa5e782b-0e66-49c6-91c5-69d4cbe4adf2\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c4fbb8a1-610b-4467-ae6e-92eb6976d152\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"2fdc4933-4fec-4fa8-acfd-78415e7657dd\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"3b824510-ba6a-49a2-82fc-a5d63739ecd3\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fe6ea208-85ad-452d-aec1-531ab8a2a404\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c413d544-3690-4917-ac62-9a1a146f4417\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"e715a464-eaea-4eb7-908b-0df083b1a6ef\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fe2f4ecc-cac0-452a-9308-bf69e176a518\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 9,
                    \"text\": \"\"
                }
            ]
        }
    ]
}"

curl -w "%{url_effective} -> %{response_code}\n" --silent --output /dev/null --location --max-time 10 \
--request PUT "${HOST}/overworld/api/v1/courses/15/worlds/2" \
--header "Content-Type: application/json" \
--data-raw "{
    \"id\": \"5aab0d50-697c-4d37-b1eb-91718edadc7c\",
    \"index\": 2,
    \"staticName\": \"Sunny Beach\",
    \"topicName\": \"\",
    \"active\": false,
    \"minigameTasks\": [
        {
            \"id\": \"6dc23f4f-e6fe-4889-9012-52a492a0116f\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 4,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"2fd46816-1ce2-4272-8188-651658a05a9e\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 9,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"effe66f4-2353-48c9-8112-0d78a6befc17\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 6,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"39069106-6855-430a-a2a9-4c64401a6d50\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 5,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"8613623b-4726-48fe-9ec2-a89aa2c5114d\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 11,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"3214ecdd-70a2-4364-8ee6-4d523bd7e904\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 7,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"828e20dd-6b2c-40b1-a91b-13105d0bbe3b\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"13c4a3d9-a838-46c2-97a9-844ff15378fc\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"c22598b7-0561-41f3-b780-7af8b80c0871\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 8,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"cb224d19-0a9b-42be-8e6f-9484bb6b6d52\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 12,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"0183e5e6-089e-4d47-baca-f0d17d023920\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 3,
            \"game\": null,
            \"configurationId\": null
        },
        {
            \"id\": \"4f9bada0-14a6-4e59-98d7-ebbd1a563906\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 10,
            \"game\": null,
            \"configurationId\": null
        }
    ],
    \"npcs\": [
        {
            \"id\": \"1c058b24-2ce5-4c62-8226-3b55879299bc\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 8,
            \"text\": \"\"
        },
        {
            \"id\": \"55eba6a7-d591-463d-a91d-30d6a512685c\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 2,
            \"text\": \"\"
        },
        {
            \"id\": \"3598b9fe-ec63-4bb4-a4d6-d8dafa5888b3\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 6,
            \"text\": \"\"
        },
        {
            \"id\": \"1b4d8579-67af-4df5-9235-a3a1154d9b81\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 10,
            \"text\": \"\"
        },
        {
            \"id\": \"a39b1d1c-52d1-4c56-8c9b-33caf370a383\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 5,
            \"text\": \"\"
        },
        {
            \"id\": \"8eff5914-2ec4-4435-a059-6a88b4fbf7f9\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 3,
            \"text\": \"\"
        },
        {
            \"id\": \"447e0612-936b-4698-93e6-576cb6d7b9ad\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 7,
            \"text\": \"\"
        },
        {
            \"id\": \"b767a942-f719-45d5-b72f-3167378cfc2b\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 9,
            \"text\": \"\"
        },
        {
            \"id\": \"71ca909f-5e3a-4d11-964c-4419923fec12\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 4,
            \"text\": \"\"
        },
        {
            \"id\": \"a2052faf-92d7-48fc-8954-a3c6cdc3faee\",
            \"area\": {
                \"worldIndex\": 2,
                \"dungeonIndex\": null
            },
            \"index\": 1,
            \"text\": \"\"
        }
    ],
    \"dungeons\": [
        {
            \"id\": \"41baea76-4b67-4110-a776-db9a8d9146bd\",
            \"index\": 1,
            \"staticName\": \"dungeon1\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"108795c1-4638-49ef-ae95-63924ed69e48\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5c1bd815-f713-41a7-841e-73af0041364e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"7fa1293b-99e0-44df-bc05-21da9460285f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"67a218b4-69b2-4685-bbf3-0472ebbfdf5d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c1040e27-c90a-486e-8418-8610745fc030\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f7f77184-87e6-4a6c-b23a-25545f4b8d73\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5ccf0b56-430b-490c-a38d-3f2e1bc0c176\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"b3c6801e-a9c7-4706-aa38-cb7f0f07c874\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"03524828-000c-4bb9-a4b7-cd130f5d5fed\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"2b478558-3097-47c8-b83f-5118a389360f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"2e6a1616-7952-4897-a539-8b7579dca6a1\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"fd91f9ae-8cb5-4cc8-913a-fd7ec4503eb1\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"7209fe7d-7f8a-4bcf-b1f4-be1b55a2861b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"db30886f-292a-4aef-a8e0-917a4fa4106e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"9a540cfb-f857-4e66-90dc-e6ac48140579\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"31143cc3-fb0c-45d9-8924-660e41b45e71\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 9,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fa18493c-754f-4de2-92a9-ca75f4f4df04\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"ac730fdd-09a7-4610-a566-6ba7cdee41ea\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"61844013-1553-4cbd-9593-27619bc09b82\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"44cbf24e-e1ef-4538-82de-658adc65ec2f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"a2237e05-ec42-428b-bfea-c508b3b016b6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"d2952bc2-9a1c-457a-8d69-bb8756c9fa90\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 1
                    },
                    \"index\": 2,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"ea7cb4dd-e151-4ceb-b495-1b10c75c97ed\",
            \"index\": 2,
            \"staticName\": \"dungeon2\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"f2069609-b97b-4d96-a4e3-6ffbfc7d0835\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"323e75bc-b5b0-42af-90d1-165845b180e7\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"3382b8c9-cf82-4181-8cfe-e53887fa9010\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"8c8546c4-671d-4535-8aa3-0ec92949d73b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"954d4134-6362-4f59-afa8-6f9c3d13ce91\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"25250335-2cfd-4319-a0ec-3e781c739bfc\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f1073d8d-c6e9-4862-9ad9-67e1877e097f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f7681c5c-97f5-4b55-889a-c6b42b1e0dfe\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e2e74bd8-24ee-43ee-a67d-8ddc65f0e16b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"9892ee1a-ccd5-4799-931c-8b4b9729b9b2\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f638ced7-41b3-40f2-a6c8-4b4c9d744596\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"1853d4f7-6659-4475-87a2-7fa5af3f71b6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"d0d94ca3-6fe4-40b5-aeaa-e1569e82066e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 9,
                    \"text\": \"\"
                },
                {
                    \"id\": \"d87cfe8e-7713-4a42-ac6a-ee51145f7d0f\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"0bd1dc76-0ed3-446c-9d25-4532b65f2dfb\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"06538435-abb0-45ea-8f0e-9a8154f94896\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c4001128-2561-4ca1-a93d-a8e9d3047037\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"51cdba16-5fad-44d4-a79c-094d4d2cdd67\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"dc4b5987-221b-4ed2-9999-a28686591880\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"3f3aeb96-7a9e-4809-b61c-d422cd588238\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"04ba88e5-2da2-4d85-822d-c0d21da9c749\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"1ec34f37-5a82-4175-9d07-2c29b4e43d09\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 2
                    },
                    \"index\": 7,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"8a86adfa-7cc1-4134-9a4c-cb056c395ab8\",
            \"index\": 3,
            \"staticName\": \"dungeon3\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"fbcf9a42-bf81-49fe-87b6-801ccb24f6f4\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"6af52a13-2f64-482e-b5bc-0bd0cc9f49a9\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"45dc2d59-1ac9-40e5-8374-87bc95398c45\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4585a4ff-4928-4ccc-812d-8e0b543fa3f3\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"95837ad5-a1f4-4596-85a7-517a03a11471\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"d27ee3d1-0bad-4572-810e-a355dc20d1ac\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c400a907-0547-4007-bc30-2ef7dd7c9063\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"f16641a7-dcd4-4fe2-bead-965e6b5da40a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"ad969c27-b57d-4fee-a359-f6e4b17391da\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e3b65bee-dc2d-4c9f-8c78-e9e0d74ee60d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"427502e5-dafd-444d-97a0-38c1f4b244aa\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"0331b5b3-108a-4cd9-9fa8-68096cd693dd\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"715ac316-31f8-4911-ac36-2a2cb69ff288\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"21996270-2424-43f5-8b6c-69972772650d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"817e9521-8dc7-4c2e-921c-313bbcc6631a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"779911c7-1d43-4bce-9cbd-e3a692bea6a6\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"57ff2b11-cdb1-4b15-bbe6-b1092d059399\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"cc33372e-0d99-4343-8ed8-c2f36db34b79\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"b878f2ba-009d-498e-a89e-08b6ec76e37b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"00863429-dd19-4bee-9b02-05dc12e6c647\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"671a94ee-02ff-41b2-b9fb-f6b670b5a454\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"ff0c0bd8-0870-42d0-af6b-824dcdde886e\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 3
                    },
                    \"index\": 9,
                    \"text\": \"\"
                }
            ]
        },
        {
            \"id\": \"d7dbd5bf-4c17-4f73-a04a-0b0904fe9353\",
            \"index\": 4,
            \"staticName\": \"dungeon4\",
            \"topicName\": \"\",
            \"active\": false,
            \"minigameTasks\": [
                {
                    \"id\": \"8fd56eaa-e602-427d-a22b-37bf79a30c2a\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 4,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5ed550a8-906e-46a1-8260-ca62bef7d21b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 1,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"307c7366-3992-4ff7-95d4-d0cb2152ac36\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 12,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4e01a8a9-c70f-491f-9ed6-26a873ba6bf9\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 3,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"c8b6926f-ef22-47cf-960d-9426afb3ba8b\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 2,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5a8f7e12-c44e-4669-91e1-ee82e52c44cc\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 10,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"e1658ed1-beee-4f69-98fb-5eb0fe3c90de\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 6,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"5e1eafba-86be-4c2f-a2e5-7136f21b39df\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 5,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"b7421b34-3fc1-445a-acb7-dcbc26423e27\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 7,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"4833e957-9170-44ba-8b88-787bef2564e5\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 11,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"d126c5d0-590e-4a8d-afda-c0832ea2756d\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 9,
                    \"game\": null,
                    \"configurationId\": null
                },
                {
                    \"id\": \"0029289d-16be-4d4e-aa88-fb2703e02630\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 8,
                    \"game\": null,
                    \"configurationId\": null
                }
            ],
            \"npcs\": [
                {
                    \"id\": \"0a5ee458-0050-4720-8dde-78cdbbcce1e8\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 7,
                    \"text\": \"\"
                },
                {
                    \"id\": \"b7466f9f-0866-427f-b8fd-5e45f32763c0\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 6,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fa5e782b-0e66-49c6-91c5-69d4cbe4adf2\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 5,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c4fbb8a1-610b-4467-ae6e-92eb6976d152\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 10,
                    \"text\": \"\"
                },
                {
                    \"id\": \"2fdc4933-4fec-4fa8-acfd-78415e7657dd\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 3,
                    \"text\": \"\"
                },
                {
                    \"id\": \"3b824510-ba6a-49a2-82fc-a5d63739ecd3\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 4,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fe6ea208-85ad-452d-aec1-531ab8a2a404\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 2,
                    \"text\": \"\"
                },
                {
                    \"id\": \"c413d544-3690-4917-ac62-9a1a146f4417\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 8,
                    \"text\": \"\"
                },
                {
                    \"id\": \"e715a464-eaea-4eb7-908b-0df083b1a6ef\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 1,
                    \"text\": \"\"
                },
                {
                    \"id\": \"fe2f4ecc-cac0-452a-9308-bf69e176a518\",
                    \"area\": {
                        \"worldIndex\": 2,
                        \"dungeonIndex\": 4
                    },
                    \"index\": 9,
                    \"text\": \"\"
                }
            ]
        }
    ]
}"

cd -
rm -r "$TEMP"