using UnityEngine;
using System.Collections;

public class SpawnerScript : MonoBehaviour {

    public float waveCalmTime = 15;
    private float waveCurrentCalmTime = 0;
    public float monstersSpawnRate = 1;
    private float monstersCurrentSpawnTime = 0;

    public int[] Wave_len = { 2 };
    private int waveLength;

    public GameObject[] monsters;
    public GameObject[] Monsters
    {
        get
        {
            return monsters;
        }
        set
        {
            monsters = value;
        }
    }

    private ArrayList monstersList;
    public ArrayList MonstersList
    {
        get
        {
            return monstersList;
        }
        set
        {
            monstersList = value;
        }
    }

    private bool isWave = false;
    private int monstersCurrentCount = 0;

	// Use this for initialization
	void Start () {
        waveCurrentCalmTime = 3;
		monstersCurrentSpawnTime = monstersSpawnRate;
        waveLength = 0;

        MonstersList = new ArrayList();
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        waveLength = MonstersList.Count;
        if (!isWave)
        {
            if (waveCurrentCalmTime > 0)
            {
                waveCurrentCalmTime -= Time.deltaTime;
            }
            else
            {
                Wave(Wave_len[0]);
                waveCurrentCalmTime = waveCalmTime;
            }
        }

        if (monstersCurrentCount > 0)
        {
            if (monstersCurrentSpawnTime > 0)
            {
                monstersCurrentSpawnTime -= Time.deltaTime;
            }
            else
            {
				Spawn (new System.Random (System.DateTime.Now.Millisecond).Next (0, 3));
                monstersCurrentSpawnTime = monstersSpawnRate;
                monstersCurrentCount -= 1;
                if (monstersCurrentCount <= 0) { isWave = false; }
            }
        }
        
	}

    void Wave(int count)
    {
        if (waveLength != 0) return;
        GameObject.Find("COUNTER").GetComponent<CounterScript>().NextWave();
        isWave = true;
        monstersCurrentCount = count;
    }

    void Spawn(int monster_type)
    {
		var newMonster = Instantiate(Monsters[monster_type]) as GameObject;
		newMonster.transform.parent = (GameObject.Find ("Zombies")).transform;
        monstersList.Add(newMonster);

        newMonster.GetComponent<Transform>().position = gameObject.GetComponent<Transform>().position;
    }
}
