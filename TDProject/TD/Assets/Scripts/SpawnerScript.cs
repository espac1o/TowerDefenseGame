using UnityEngine;
using System.Collections;

public class SpawnerScript : MonoBehaviour {

    public float calm_time = 15;
    private float curr_calm_time = 0;
    public float spawn_rate = 1;
    private float curr_spawn_time = 0;

    public int[] Wave_len = { 2 };
    public int waveLength;

    public GameObject target;
    public GameObject[] monsters;

    public float speed = 4;
    public float hp = 3;
    public float damage = 1f;

    private bool is_Wave = false;
    private int curr_count = 0;


    public ArrayList monsters_list = new ArrayList();

	// Use this for initialization
	void Start () {
        curr_calm_time = 3;
		curr_spawn_time = spawn_rate;
        target = null;
        waveLength = 0;
	}
	
	// Update is called once per frame
	void FixedUpdate () {
        waveLength = monsters_list.Count;
        if (!is_Wave)
        {
            if (curr_calm_time > 0)
            {
                curr_calm_time -= Time.deltaTime;
            }
            else
            {
                Wave(Wave_len[0]);
                curr_calm_time = calm_time;
            }
        }

        if (curr_count > 0)
        {
            if (curr_spawn_time > 0)
            {
                curr_spawn_time -= Time.deltaTime;
            }
            else
            {
				spawn (new System.Random (System.DateTime.Now.Millisecond).Next (0, 2));
                curr_spawn_time = spawn_rate;
                curr_count -= 1;
                if (curr_count <= 0) { is_Wave = false; }
            }
        }
        
	}

    void Wave(int count)
    {
        if (waveLength != 0) return;
        GameObject.Find("COUNTER").GetComponent<CounterScript>().NextWave();
        is_Wave = true;
        curr_count = count;
    }

    void spawn(int monster_type)
    {
		var newMonster = Instantiate(monsters[monster_type]) as GameObject;
		newMonster.transform.parent = (GameObject.Find ("Zombies")).transform;
        monsters_list.Add(newMonster);
 

        newMonster.GetComponent<Transform>().position = gameObject.GetComponent<Transform>().position;
        newMonster.GetComponent<UnitScript>().speed = speed;
        newMonster.GetComponent<UnitScript>().damage = damage;
        newMonster.GetComponent<UnitScript>().hp = hp;
    }
}
